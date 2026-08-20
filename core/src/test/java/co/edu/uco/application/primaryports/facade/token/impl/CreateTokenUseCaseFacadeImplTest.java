package co.edu.uco.application.primaryports.facade.token.impl;

import co.edu.uco.application.common.mapper.dto.impl.TokenDTOMapper;
import co.edu.uco.application.primaryports.dto.keypair.KeyPairDTO;
import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.secret.CreateTokenSecretPort;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.application.usecase.handling.HandlingCreateTokenPort;
import co.edu.uco.application.usecase.handling.HandlingRevokeTokenPort;
import co.edu.uco.application.usecase.validator.token.CreateTokenCompositeValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTokenUseCaseFacadeImplTest {

    @Mock
    private HandlingCreateTokenPort handlingCreateTokenPort;
    @Mock
    private TokenDTOMapper tokenDTOMapper;
    @Mock
    private EncryptTokenPort encrypt;
    @Mock
    private CreateTokenSecretPort createTokenSecretPort;
    @Mock
    private CreateTokenCompositeValidator validator;
    @Mock
    private HandlingRevokeTokenPort handlingRevokeTokenPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private CatalogPort catalogPort;

    private CreateTokenUseCaseFacadeImpl facade;

    private final String applicationId = "123e4567-e89b-12d3-a456-426614174000";
    private final String environmentId = "123e4567-e89b-12d3-a456-426614174100";

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateTokenUseCaseFacadeImpl.class)).thenReturn(log);
        facade = new CreateTokenUseCaseFacadeImpl(
                handlingCreateTokenPort, tokenDTOMapper, encrypt, createTokenSecretPort,
                validator, handlingRevokeTokenPort, loggerFactory, catalogPort);
    }

    private CreateTokenDTO validDto() {
        CreateTokenDTO dto = new CreateTokenDTO();
        dto.setEnvironmentId(environmentId);
        dto.setExpirationDate(LocalDateTime.now().plusDays(30).toString());
        return dto;
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    @Test
    void execute_returnsSignature_whenFlowSucceeds() throws Exception {
        CreateTokenDTO dto = validDto();
        KeyPair keyPair = generateKeyPair();
        KeyPairDTO keyPairDTO = new KeyPairDTO(keyPair.getPublic(), keyPair.getPrivate());
        when(encrypt.generateKeys()).thenReturn(keyPairDTO);
        String signature = "signature-123";
        when(encrypt.generateSignature(any(String.class), any(PublicKey.class))).thenReturn(signature);

        String result = facade.execute(dto, applicationId);

        assertThat(result).isEqualTo(signature);
        verify(validator).validate(dto, applicationId);
        verify(handlingRevokeTokenPort).execute(environmentId, "123e4567-e89b-12d3-a456-426614175000");

        ArgumentCaptor<String> secretNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(createTokenSecretPort).execute(secretNameCaptor.capture(), any(String.class));
        assertThat(secretNameCaptor.getValue())
                .startsWith("UCOLAB_TOKEN_PRIVATE_KEY_")
                .contains("123E4567_E89B_12D3_A456_426614174000")
                .contains("123E4567_E89B_12D3_A456_426614174100");

        verify(handlingCreateTokenPort).createToken(any());
    }

    @Test
    void execute_throwsCrossWordsException_whenGenerateKeysReturnsNull() {
        CreateTokenDTO dto = validDto();
        when(encrypt.generateKeys()).thenReturn(null);
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_024.getCode())).thenReturn("Key generation failed");

        assertThatThrownBy(() -> facade.execute(dto, applicationId))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("Key generation failed"));
    }

    @Test
    void execute_throwsCrossWordsException_whenSignatureGenerationFails() throws Exception {
        CreateTokenDTO dto = validDto();
        KeyPair keyPair = generateKeyPair();
        KeyPairDTO keyPairDTO = new KeyPairDTO(keyPair.getPublic(), keyPair.getPrivate());
        when(encrypt.generateKeys()).thenReturn(keyPairDTO);
        when(encrypt.generateSignature(any(String.class), any(PublicKey.class)))
                .thenThrow(new IllegalStateException("crypto error"));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_025.getCode())).thenReturn("Token creation failed");

        assertThatThrownBy(() -> facade.execute(dto, applicationId))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("Token creation failed"));
    }

    @Test
    void execute_throwsCrossWordsException_whenSecretStorageFails() throws Exception {
        CreateTokenDTO dto = validDto();
        KeyPair keyPair = generateKeyPair();
        KeyPairDTO keyPairDTO = new KeyPairDTO(keyPair.getPublic(), keyPair.getPrivate());
        when(encrypt.generateKeys()).thenReturn(keyPairDTO);
        when(encrypt.generateSignature(any(String.class), any(PublicKey.class))).thenReturn("sig");
        org.mockito.Mockito.doThrow(new IllegalStateException("storage down"))
                .when(createTokenSecretPort).execute(any(String.class), any(String.class));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_025.getCode())).thenReturn("Token creation failed");

        assertThatThrownBy(() -> facade.execute(dto, applicationId))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("Token creation failed"));
    }
}
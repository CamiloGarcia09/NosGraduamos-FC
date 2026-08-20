package co.edu.uco.infraestructure.secondaryadapters.encryption;

import co.edu.uco.application.primaryports.dto.keypair.KeyPairDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaSecurityEncryptTokenAdapterTest {

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private JavaSecurityEncryptTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient().when(loggerFactory.getLogger(JavaSecurityEncryptTokenAdapter.class)).thenReturn(log);
        adapter = new JavaSecurityEncryptTokenAdapter(catalogPort, loggerFactory);
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private String privateKeyBase64(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    @Test
    void generateKeys_returnsKeyPairWithPublicAndPrivateKeys() throws Exception {
        KeyPairDTO keyPair = adapter.generateKeys();

        assertThat(keyPair.getPublicKey()).isNotNull();
        assertThat(keyPair.getPrivateKey()).isNotNull();
    }

    @Test
    void generateSignature_returnsBase64EncryptedData() throws Exception {
        KeyPair keyPair = generateKeyPair();

        String signature = adapter.generateSignature("secret", keyPair.getPublic());

        assertThat(signature).isNotBlank();
        assertThat(Base64.getDecoder().decode(signature)).isNotEmpty();
    }

    @Test
    void generateSignature_throwsCrossWordsException_whenDataIsNull() {
        when(catalogPort.getMessage("TCH_040")).thenReturn("null data");
        when(catalogPort.getMessage("FUN_025")).thenReturn("technical");

        assertThatThrownBy(() -> adapter.generateSignature(null, generateKeyPair().getPublic()))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> {
                    CrossWordsException cwe = (CrossWordsException) ex;
                    assertThat(cwe.getTechnicalMessage()).isEqualTo("null data");
                    assertThat(cwe.getUserMessage()).isEqualTo("technical");
                });
    }

    @Test
    void generateSignature_throwsCrossWordsException_whenPublicKeyIsNull() throws Exception {
        when(catalogPort.getMessage("TCH_040")).thenReturn("null key");
        when(catalogPort.getMessage("FUN_025")).thenReturn("technical");

        assertThatThrownBy(() -> adapter.generateSignature("data", null))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("null key"));
    }

    @Test
    void generateSignature_throwsCrossWordsException_whenKeyAlgorithmIsIncompatible() throws Exception {
        KeyPairGenerator dsaGenerator = KeyPairGenerator.getInstance("DSA");
        dsaGenerator.initialize(2048);
        KeyPair dsaKeyPair = dsaGenerator.generateKeyPair();
        when(catalogPort.getMessage("TCH_027")).thenReturn("cipher error");
        when(catalogPort.getMessage("FUN_025")).thenReturn("technical");

        assertThatThrownBy(() -> adapter.generateSignature("data", dsaKeyPair.getPublic()))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("cipher error"));
    }

    @Test
    void access_returnsTrue_whenSignatureMatchesSecretName() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String secretName = "my-secret";
        String signature = adapter.generateSignature(secretName, keyPair.getPublic());

        Boolean result = adapter.access(privateKeyBase64(keyPair), signature, secretName);

        assertThat(result).isTrue();
    }

    @Test
    void access_returnsFalse_whenSecretNameDoesNotMatch() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String signature = adapter.generateSignature("my-secret", keyPair.getPublic());

        Boolean result = adapter.access(privateKeyBase64(keyPair), signature, "other-secret");

        assertThat(result).isFalse();
    }

    @Test
    void access_returnsFalse_whenArgumentsAreEmpty() {
        Boolean result = adapter.access(" ", " ", " ");

        assertThat(result).isFalse();
    }

    @Test
    void access_returnsFalse_whenSignatureIsInvalidBase64() throws Exception {
        KeyPair keyPair = generateKeyPair();

        Boolean result = adapter.access(privateKeyBase64(keyPair), "!!!not-base64!!!", "my-secret");

        assertThat(result).isFalse();
    }

    @Test
    void access_returnsFalse_whenPrivateKeyIsInvalidBase64() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String signature = adapter.generateSignature("my-secret", keyPair.getPublic());

        Boolean result = adapter.access("!!!not-base64!!!", signature, "my-secret");

        assertThat(result).isFalse();
    }
}
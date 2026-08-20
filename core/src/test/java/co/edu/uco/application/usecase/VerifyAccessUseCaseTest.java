package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenCachePort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyAccessUseCaseTest {

    @Mock
    private EncryptTokenPort encryptTokenPort;
    @Mock
    private FindTokenCachePort findTokenCachePort;
    @Mock
    private FindTokenRepository findTokenRepository;
    @Mock
    private TokenStateRepository tokenStateRepository;
    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private VerifyAccessUseCase useCase;

    private final UUID activeStateId = UUID.randomUUID();

    private TokenData activeToken(String token) {
        TokenData data = new TokenData();
        data.setStateId(activeStateId.toString());
        data.setSecretName("secret-1");
        return data;
    }

    @Test
    void verifyAccess_returnsTrue_whenAccessIsGranted() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken("token"));
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenReturn(true);

        assertThat(useCase.verifyAccess("token")).isTrue();
    }

    @Test
    void verifyAccess_returnsFalse_whenEncryptionFails() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken("token"));
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenThrow(new IllegalStateException("boom"));

        assertThat(useCase.verifyAccess("token")).isFalse();
    }

    @Test
    void verifyAccess_returnsFalse_whenAccessDenied() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken("token"));
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenReturn(false);

        assertThat(useCase.verifyAccess("token")).isFalse();
    }

    @Test
    void verifyAccess_throwsBusinessRuleException_whenTokenIsInactive() {
        when(findTokenRepository.findById("token")).thenReturn(activeToken("token"));
        StatusTokenData inactive = new StatusTokenData(activeStateId, "Inactive");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(inactive);
        when(catalogPort.getMessage("TCH_033")).thenReturn("Token is not active");

        assertThatThrownBy(() -> useCase.verifyAccess("token"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("Token is not active"));
    }
}
package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenCachePort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyAccessUseCaseTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 15, 15, 30, 45);

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

    private VerifyAccessUseCase useCase;

    private final UUID activeStateId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-15T15:30:45Z"), ZoneOffset.UTC);
        useCase = new VerifyAccessUseCase(encryptTokenPort, findTokenCachePort, findTokenRepository,
                tokenStateRepository, catalogPort, clock);
    }

    private TokenData activeToken() {
        TokenData data = new TokenData();
        data.setStateId(activeStateId.toString());
        data.setSecretName("secret-1");
        data.setExpirationDate(NOW.plusMinutes(5));
        return data;
    }

    @Test
    void verifyAccess_returnsTrue_whenAccessIsGranted() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken());
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenReturn(true);

        assertThat(useCase.verifyAccess("token")).isTrue();
    }

    @Test
    void verifyAccess_returnsFalse_whenEncryptionFails() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken());
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenThrow(new IllegalStateException("boom"));

        assertThat(useCase.verifyAccess("token")).isFalse();
    }

    @Test
    void verifyAccess_returnsFalse_whenAccessDenied() throws Exception {
        when(findTokenRepository.findById("token")).thenReturn(activeToken());
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(findTokenCachePort.getSecret("secret-1"))
                .thenReturn(Map.of("privateKey", "pk", "secretName", "secret-1"));
        when(encryptTokenPort.access("pk", "token", "secret-1")).thenReturn(false);

        assertThat(useCase.verifyAccess("token")).isFalse();
    }

    @Test
    void verifyAccess_throwsUnauthorizedException_whenTokenIsInactive() {
        when(findTokenRepository.findById("token")).thenReturn(activeToken());
        StatusTokenData inactive = new StatusTokenData(activeStateId, "Inactive");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(inactive);
        when(catalogPort.getMessage("TCH_033")).thenReturn("Token is not active");

        assertThatThrownBy(() -> useCase.verifyAccess("token"))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat((UnauthorizedException) ex)
                        .extracting(UnauthorizedException::getUserMessage, UnauthorizedException::getHttpStatus)
                        .containsExactly("Token is not active", 401));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0})
    void verifyAccess_throwsUnauthorizedException_whenTokenIsExpired(long secondsFromNow) {
        TokenData tokenData = activeToken();
        tokenData.setExpirationDate(NOW.plusSeconds(secondsFromNow));
        when(findTokenRepository.findById("token")).thenReturn(tokenData);
        StatusTokenData active = new StatusTokenData(activeStateId, "Active");
        when(tokenStateRepository.findByStatus(activeStateId.toString())).thenReturn(active);
        when(catalogPort.getMessage("TCH_033")).thenReturn("Token is expired or inactive");

        assertThatThrownBy(() -> useCase.verifyAccess("token"))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat((UnauthorizedException) ex)
                        .extracting(UnauthorizedException::getUserMessage, UnauthorizedException::getHttpStatus)
                        .containsExactly("Token is expired or inactive", 401));
        verifyNoInteractions(findTokenCachePort, encryptTokenPort);
    }

    @Test
    void verifyAccess_throwsUnauthorizedException_whenTokenDoesNotExist() {
        when(findTokenRepository.findById("invalid-token"))
                .thenThrow(NotFoundException.buildUserException("Token not found"));
        when(catalogPort.getMessage("TCH_031")).thenReturn("Invalid token");

        assertThatThrownBy(() -> useCase.verifyAccess("invalid-token"))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> assertThat((UnauthorizedException) ex)
                        .extracting(UnauthorizedException::getUserMessage, UnauthorizedException::getHttpStatus)
                        .containsExactly("Invalid token", 401));
        verifyNoInteractions(tokenStateRepository, findTokenCachePort, encryptTokenPort);
    }
}

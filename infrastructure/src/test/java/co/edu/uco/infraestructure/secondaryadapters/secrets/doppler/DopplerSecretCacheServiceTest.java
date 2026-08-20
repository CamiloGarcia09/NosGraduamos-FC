package co.edu.uco.infraestructure.secondaryadapters.secrets.doppler;

import co.edu.uco.application.secondaryports.secret.SecretProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DopplerSecretCacheServiceTest {

    @Mock
    private SecretProviderPort secretProviderPort;

    private DopplerSecretCacheService service;

    @BeforeEach
    void setUp() {
        service = new DopplerSecretCacheService(secretProviderPort);
    }

    @Test
    void getSecret_loadsFromProviderOnFirstCall() {
        Map<String, String> secret = Map.of("privateKey", "key");
        when(secretProviderPort.findSecretToken("secret-1")).thenReturn(secret);

        assertThat(service.getSecret("secret-1")).isEqualTo(secret);
        verify(secretProviderPort).findSecretToken("secret-1");
    }

    @Test
    void getSecret_returnsCachedValueOnSecondCall() {
        Map<String, String> secret = Map.of("privateKey", "key");
        when(secretProviderPort.findSecretToken("secret-1")).thenReturn(secret);

        service.getSecret("secret-1");
        service.getSecret("secret-1");

        verify(secretProviderPort, times(1)).findSecretToken("secret-1");
    }

    @Test
    void invalidateCache_forcesReload() {
        Map<String, String> secret = Map.of("privateKey", "key");
        when(secretProviderPort.findSecretToken("secret-1")).thenReturn(secret);

        service.getSecret("secret-1");
        service.invalidateCache("secret-1");
        service.getSecret("secret-1");

        verify(secretProviderPort, times(2)).findSecretToken("secret-1");
    }
}
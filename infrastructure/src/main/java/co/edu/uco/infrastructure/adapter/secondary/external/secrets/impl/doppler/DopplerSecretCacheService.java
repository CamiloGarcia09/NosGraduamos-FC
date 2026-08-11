package co.edu.uco.infrastructure.adapter.secondary.external.secrets.impl.doppler;

import co.edu.uco.core.domain.port.out.repository.token.FindTokenCachePort;
import co.edu.uco.core.domain.port.out.secret.SecretProviderPort;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.CACHE_EXPIRATION_TIME;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.CACHE_MAXIMUM_SIZE;

@Component
public final class DopplerSecretCacheService  implements FindTokenCachePort {
    private final Cache<String, Map<String, String>> dopplerSecretCache;
    private final SecretProviderPort secretProviderPort;
    public DopplerSecretCacheService(SecretProviderPort secretProviderPort) {
        this.secretProviderPort = secretProviderPort;
        this.dopplerSecretCache = Caffeine.newBuilder()
                .expireAfterWrite(CACHE_EXPIRATION_TIME, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAXIMUM_SIZE)
                .build();
    }
    @Override
    public Map<String, String> getSecret(String secretName) {
        return dopplerSecretCache.get(secretName, secretProviderPort::findSecretToken);
    }
    public void invalidateCache(String secretName) {
        dopplerSecretCache.invalidate(secretName);
    }
}
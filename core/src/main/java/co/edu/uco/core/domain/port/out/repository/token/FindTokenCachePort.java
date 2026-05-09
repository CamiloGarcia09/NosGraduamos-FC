package co.edu.uco.core.domain.port.out.repository.token;

import java.util.Map;

public interface FindTokenCachePort {
    Map<String, String> getSecret(String secretName);
}
package co.edu.uco.application.secondaryports.repository.token;

import java.util.Map;

public interface FindTokenCachePort {
    Map<String, String> getSecret(String secretName);
}
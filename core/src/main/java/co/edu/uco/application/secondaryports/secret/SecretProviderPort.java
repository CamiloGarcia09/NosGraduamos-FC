package co.edu.uco.application.secondaryports.secret;

import java.util.Map;

public interface SecretProviderPort {
    Map<String, String> findSecretToken(String secretName);
}

package co.edu.uco.core.domain.port.out.secret;

import java.util.Map;

public interface SecretProviderPort {
    Map<String, String> findSecretToken(String secretName);
}

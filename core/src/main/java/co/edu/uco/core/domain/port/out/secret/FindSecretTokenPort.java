package co.edu.uco.core.domain.port.out.secret;

import java.util.Map;

public interface FindSecretTokenPort {
    Map<String, String> findSecretToken(String secretName);
}

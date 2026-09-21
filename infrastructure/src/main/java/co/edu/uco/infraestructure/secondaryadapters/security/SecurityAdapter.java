package co.edu.uco.infraestructure.secondaryadapters.security;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.security.SecurityPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "components.security", name = "enabled", havingValue = "false", matchIfMissing = true)
public final class SecurityAdapter implements SecurityPort {

    private static final String BURNED_ACCESS_TOKEN = "BURNED_ACCESS_TOKEN_NOSGRADUAMOS_2026";

    private final LoggingPort log;

    public SecurityAdapter(LoggingPortFactory loggingPortFactory) {
        this.log = loggingPortFactory.getLogger(SecurityAdapter.class);
    }

    @Override
    public String generateAccessToken(String applicationId, String environmentId, LocalDateTime expirationDate) {
        log.info("Generando token quemado para la aplicacion [%s] y entorno [%s]".formatted(applicationId, environmentId));
        return BURNED_ACCESS_TOKEN;
    }

    @Override
    public boolean validateAccessToken(String token) {
        boolean valid = BURNED_ACCESS_TOKEN.equals(token);
        log.info("Validando token quemado: [%s] -> %s".formatted(token, valid));
        return valid;
    }
}
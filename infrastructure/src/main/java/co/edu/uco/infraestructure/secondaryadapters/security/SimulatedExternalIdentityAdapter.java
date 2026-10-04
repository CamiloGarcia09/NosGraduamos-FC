package co.edu.uco.infraestructure.secondaryadapters.security;

import co.edu.uco.application.secondaryports.security.ExternalIdentityResolverPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@ConditionalOnProperty(
        prefix = "components.security.simulated",
        name = "enabled",
        havingValue = "true"
)
public final class SimulatedExternalIdentityAdapter implements ExternalIdentityResolverPort {

    private static final String INVALID_TOKEN_MESSAGE = "External access token is invalid or expired";

    private final String expectedToken;
    private final ExternalIdentity identity;
    private final Clock clock;

    @Autowired
    public SimulatedExternalIdentityAdapter(
            @Value("${components.security.simulated.token:}") String expectedToken,
            @Value("${components.security.simulated.issuer:}") String issuer,
            @Value("${components.security.simulated.subject:}") String subject,
            @Value("${components.security.simulated.email:}") String email,
            @Value("${components.security.simulated.expiration:1970-01-01T00:00:00Z}") String expiration) {
        this(expectedToken, issuer, subject, email, Instant.parse(expiration), Clock.systemUTC());
    }

    SimulatedExternalIdentityAdapter(String expectedToken, String issuer, String subject, String email,
                                      Instant expiration, Clock clock) {
        this.expectedToken = expectedToken;
        this.identity = new ExternalIdentity(issuer, subject, email, expiration);
        this.clock = clock;
    }

    @Override
    public ExternalIdentity resolve(String accessToken) {
        if (expectedToken.isBlank() || !expectedToken.equals(accessToken)
                || !identity.expiration().isAfter(clock.instant())) {
            throw UnauthorizedException.buildUserException(INVALID_TOKEN_MESSAGE);
        }
        return identity;
    }
}

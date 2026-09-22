package co.edu.uco.application.usecase.domain.security;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalIdentityTest {

    @Test
    void constructor_exposesOnlyExternalPrincipalAttributes() {
        Instant expiration = Instant.parse("2030-01-01T00:00:00Z");

        ExternalIdentity identity = new ExternalIdentity(
                "https://issuer.example", "subject-1", "user@example.com", PrincipalType.HUMAN, expiration);

        assertThat(identity)
                .extracting(
                        ExternalIdentity::issuer,
                        ExternalIdentity::subject,
                        ExternalIdentity::email,
                        ExternalIdentity::principalType,
                        ExternalIdentity::expiration
                )
                .containsExactly(
                        "https://issuer.example", "subject-1", "user@example.com", PrincipalType.HUMAN, expiration);
    }

    @Test
    void constructor_preservesMissingEmail_whenProviderDoesNotSupplyIt() {
        ExternalIdentity identity = new ExternalIdentity(
                "https://issuer.example", "service-1", null, PrincipalType.SERVICE, Instant.MAX);

        assertThat(identity.email()).isNull();
    }

    @Test
    void values_containsSupportedPrincipalTypes() {
        assertThat(PrincipalType.values()).containsExactly(PrincipalType.HUMAN, PrincipalType.SERVICE);
    }
}

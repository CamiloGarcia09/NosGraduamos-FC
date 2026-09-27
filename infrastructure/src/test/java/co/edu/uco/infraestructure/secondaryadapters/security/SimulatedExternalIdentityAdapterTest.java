package co.edu.uco.infraestructure.secondaryadapters.security;

import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulatedExternalIdentityAdapterTest {

    private static final Instant NOW = Instant.parse("2026-09-22T12:00:00Z");
    private static final String TOKEN = "simulated-access-token";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SimulatedExternalIdentityAdapter.class);

    @Test
    void component_isNotCreated_whenSimulatorIsNotExplicitlyEnabled() {
        contextRunner.run(context -> assertThat(context)
                .doesNotHaveBean(SimulatedExternalIdentityAdapter.class));
    }

    @Test
    void component_isCreated_whenSimulatorIsExplicitlyEnabled() {
        contextRunner
                .withPropertyValues(
                        "components.security.simulated.enabled=true",
                        "components.security.simulated.token=simulated-access-token",
                        "components.security.simulated.issuer=https://issuer.example",
                        "components.security.simulated.subject=subject-1",
                        "components.security.simulated.email=user@example.com",
                        "components.security.simulated.principal-type=HUMAN",
                        "components.security.simulated.expiration=2999-01-01T00:00:00Z"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(SimulatedExternalIdentityAdapter.class);
                    var identity = context.getBean(SimulatedExternalIdentityAdapter.class).resolve(TOKEN);
                    assertThat(identity)
                            .extracting(
                                    ExternalIdentity::issuer,
                                    ExternalIdentity::subject,
                                    ExternalIdentity::email,
                                    ExternalIdentity::principalType,
                                    ExternalIdentity::expiration
                            )
                            .containsExactly(
                                    "https://issuer.example",
                                    "subject-1",
                                    "user@example.com",
                                    PrincipalType.HUMAN,
                                    Instant.parse("2999-01-01T00:00:00Z")
                            );
                });
    }

    @Test
    void resolve_returnsStableIdentity_whenTokenIsValidAndNotExpired() {
        var adapter = adapterWithExpiration(NOW.plusSeconds(60));

        var identity = adapter.resolve(TOKEN);

        assertThat(identity)
                .extracting(
                        ExternalIdentity::issuer,
                        ExternalIdentity::subject,
                        ExternalIdentity::email,
                        ExternalIdentity::principalType,
                        ExternalIdentity::expiration
                )
                .containsExactly(
                        "https://issuer.example",
                        "subject-1",
                        "user@example.com",
                        PrincipalType.HUMAN,
                        NOW.plusSeconds(60)
                );
    }

    @Test
    void resolve_throwsUnauthorized_whenTokenIsInvalid() {
        var adapter = adapterWithExpiration(NOW.plusSeconds(60));

        assertThatThrownBy(() -> adapter.resolve("different-token"))
                .isInstanceOf(UnauthorizedException.class)
                .extracting(exception -> ((UnauthorizedException) exception).getUserMessage())
                .isEqualTo("External access token is invalid or expired");
    }

    @Test
    void resolve_throwsUnauthorized_whenConfiguredTokenIsEmpty() {
        var adapter = new SimulatedExternalIdentityAdapter(
                "", "issuer", "subject", "email", PrincipalType.SERVICE,
                NOW.plusSeconds(60), Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> adapter.resolve(""))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void resolve_throwsUnauthorized_whenTokenIsExpired() {
        var adapter = adapterWithExpiration(NOW.minusSeconds(1));

        assertThatThrownBy(() -> adapter.resolve(TOKEN))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void resolve_throwsUnauthorized_whenTokenExpiresAtCurrentInstant() {
        var adapter = adapterWithExpiration(NOW);

        assertThatThrownBy(() -> adapter.resolve(TOKEN))
                .isInstanceOf(UnauthorizedException.class);
    }

    private SimulatedExternalIdentityAdapter adapterWithExpiration(Instant expiration) {
        return new SimulatedExternalIdentityAdapter(
                TOKEN,
                "https://issuer.example",
                "subject-1",
                "user@example.com",
                PrincipalType.HUMAN,
                expiration,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }
}

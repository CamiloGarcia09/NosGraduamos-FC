package co.edu.uco.application.usecase.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDomainTest {

    @Test
    void noArgsConstructor_createsEmptyDomain() {
        TokenDomain domain = new TokenDomain();

        assertThat(domain.getId()).isNull();
        assertThat(domain.getSecretName()).isNull();
        assertThat(domain.getEnvironmentId()).isNull();
        assertThat(domain.getStateId()).isNull();
    }

    @Test
    void parameterizedConstructor_setsValues() {
        UUID envId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        TokenDomain domain = new TokenDomain("id-1", now, now.plusDays(1), envId, "secret", stateId);

        assertThat(domain.getId()).isEqualTo("id-1");
        assertThat(domain.getSecretName()).isEqualTo("secret");
        assertThat(domain.getCreationDate()).isEqualTo(now);
        assertThat(domain.getExpirationDate()).isEqualTo(now.plusDays(1));
        assertThat(domain.getEnvironmentId()).isEqualTo(envId);
        assertThat(domain.getStateId()).isEqualTo(stateId);
    }

    @Test
    void create_buildsInstance() {
        TokenDomain domain = new TokenDomain().create("id", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID(), "secret", UUID.randomUUID());

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo("id");
    }

    @Test
    void setters_normalizeValues() {
        UUID envId = UUID.randomUUID();
        TokenDomain domain = new TokenDomain();
        domain.setId("  id  ");
        domain.setSecretName("  secret  ");
        domain.setEnvironmentId(envId);

        assertThat(domain.getId()).isEqualTo("id");
        assertThat(domain.getSecretName()).isEqualTo("secret");
        assertThat(domain.getEnvironmentId()).isEqualTo(envId);
    }

    @Test
    void setEnvironmentId_usesDefaultWhenNull() {
        TokenDomain domain = new TokenDomain();
        domain.setEnvironmentId(null);

        assertThat(domain.getEnvironmentId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setExpirationDate_usesDefaultTimeWhenNull() {
        TokenDomain domain = new TokenDomain();
        domain.setExpirationDate(null);

        assertThat(domain.getExpirationDate()).isNotNull();
    }

    @Test
    void setCreationDate_usesDefaultTimeWhenNull() {
        TokenDomain domain = new TokenDomain();
        domain.setCreationDate(null);

        assertThat(domain.getCreationDate()).isNotNull();
    }
}
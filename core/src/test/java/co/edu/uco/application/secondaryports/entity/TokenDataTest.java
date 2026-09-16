package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC);
        TokenData data = new TokenData();
        LocalDateTime after = LocalDateTime.now(ZoneOffset.UTC);

        assertThat(data.getId()).isEmpty();
        assertThat(data.getSecretName()).isEmpty();
        assertThat(data.getEnvironmentId()).isEmpty();
        assertThat(data.getStateId()).isEmpty();
        assertThat(data.getCreationDate()).isBetween(before, after);
        assertThat(data.getExpirationDate()).isBetween(before, after);
    }

    @Test
    void parameterizedConstructor_storesValues() {
        LocalDateTime now = LocalDateTime.now();
        TokenData data = new TokenData(" id ", now, now, " env ", " secret ", " state ");

        assertThat(data.getId()).isEqualTo("id");
        assertThat(data.getSecretName()).isEqualTo("secret");
        assertThat(data.getEnvironmentId()).isEqualTo("env");
        assertThat(data.getStateId()).isEqualTo("state");
        assertThat(data.getCreationDate()).isEqualTo(now);
        assertThat(data.getExpirationDate()).isEqualTo(now);
    }

    @Test
    void setters_applyDefaultsWhenNull() {
        TokenData data = new TokenData();
        LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC);

        data.setCreationDate(null);
        data.setExpirationDate(null);
        data.setId(null);
        data.setSecretName(null);
        data.setEnvironmentId(null);
        data.setStateId(null);
        LocalDateTime after = LocalDateTime.now(ZoneOffset.UTC);

        assertThat(data.getCreationDate()).isBetween(before, after);
        assertThat(data.getExpirationDate()).isBetween(before, after);
        assertThat(data.getId()).isEmpty();
        assertThat(data.getSecretName()).isEmpty();
        assertThat(data.getEnvironmentId()).isEmpty();
        assertThat(data.getStateId()).isEmpty();
    }
}

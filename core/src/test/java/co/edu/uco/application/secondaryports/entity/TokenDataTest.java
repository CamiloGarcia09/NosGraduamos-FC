package co.edu.uco.application.secondaryports.entity;

import co.edu.uco.crosscutting.helpers.UtilDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        TokenData data = new TokenData();

        assertThat(data.getId()).isEmpty();
        assertThat(data.getSecretName()).isEmpty();
        assertThat(data.getEnvironmentId()).isEmpty();
        assertThat(data.getStateId()).isEmpty();
        assertThat(data.getCreationDate()).isEqualTo(UtilDate.TIME);
        assertThat(data.getExpirationDate()).isEqualTo(UtilDate.TIME);
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

        data.setCreationDate(null);
        data.setExpirationDate(null);
        data.setId(null);
        data.setSecretName(null);
        data.setEnvironmentId(null);
        data.setStateId(null);

        assertThat(data.getCreationDate()).isEqualTo(UtilDate.TIME);
        assertThat(data.getExpirationDate()).isEqualTo(UtilDate.TIME);
        assertThat(data.getId()).isEmpty();
        assertThat(data.getSecretName()).isEmpty();
        assertThat(data.getEnvironmentId()).isEmpty();
        assertThat(data.getStateId()).isEmpty();
    }
}
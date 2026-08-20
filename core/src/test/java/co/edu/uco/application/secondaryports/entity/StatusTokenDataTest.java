package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StatusTokenDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        StatusTokenData data = new StatusTokenData();

        assertThat(data.getId()).isNotNull();
        assertThat(data.getName()).isEmpty();
    }

    @Test
    void parameterizedConstructor_storesValues() {
        UUID id = UUID.randomUUID();

        StatusTokenData data = new StatusTokenData(id, "  active  ");

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getName()).isEqualTo("active");
    }

    @Test
    void setId_usesDefaultUUIDWhenNull() {
        StatusTokenData data = new StatusTokenData();

        data.setId(null);

        assertThat(data.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setName_trimsValue() {
        StatusTokenData data = new StatusTokenData();

        data.setName("  active  ");

        assertThat(data.getName()).isEqualTo("active");
    }

    @Test
    void build_returnsDefaultInstance() {
        StatusTokenData data = StatusTokenData.build();

        assertThat(data).isNotNull();
        assertThat(data.getId()).isNotNull();
        assertThat(data.getName()).isEmpty();
    }
}

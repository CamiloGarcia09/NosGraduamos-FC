package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentTypeTest {

    @Test
    void defaultConstructor_setsDefaults() {
        EnvironmentType type = new EnvironmentType();

        assertThat(type.getId()).isNotNull();
        assertThat(type.getName()).isEmpty();
    }

    @Test
    void parameterizedConstructor_storesValues() {
        UUID id = UUID.randomUUID();
        EnvironmentType type = new EnvironmentType(id, "dev");

        assertThat(type.getId()).isEqualTo(id);
        assertThat(type.getName()).isEqualTo("dev");
    }

    @Test
    void setName_trimsValue() {
        EnvironmentType type = new EnvironmentType();

        type.setName("  dev  ");

        assertThat(type.getName()).isEqualTo("dev");
    }

    @Test
    void setId_usesDefaultWhenNull() {
        EnvironmentType type = new EnvironmentType();

        type.setId(null);

        assertThat(type.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void build_returnsDefaultInstance() {
        EnvironmentType type = EnvironmentType.build();

        assertThat(type).isNotNull();
        assertThat(type.getName()).isEmpty();
    }
}
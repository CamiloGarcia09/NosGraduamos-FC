package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        ParameterData data = new ParameterData();

        assertThat(data.getId()).isNotNull();
        assertThat(data.getName()).isEmpty();
        assertThat(data.getDescription()).isEmpty();
        assertThat(data.getMessage()).isNull();
    }

    @Test
    void parameterizedConstructor_storesValues() {
        UUID id = UUID.randomUUID();
        MessageData message = MessageData.build();
        ParameterData data = new ParameterData(id, message, " name ", " description ");

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getMessage()).isSameAs(message);
        assertThat(data.getName()).isEqualTo("name");
        assertThat(data.getDescription()).isEqualTo("description");
    }

    @Test
    void setId_usesDefaultWhenNull() {
        ParameterData data = new ParameterData();

        data.setId(null);

        assertThat(data.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setMessage_usesDefaultWhenNull() {
        ParameterData data = new ParameterData();

        data.setMessage(null);

        assertThat(data.getMessage()).isNotNull();
    }

    @Test
    void setters_trimTextFields() {
        ParameterData data = new ParameterData();

        data.setName("  x  ");
        data.setDescription("  y  ");

        assertThat(data.getName()).isEqualTo("x");
        assertThat(data.getDescription()).isEqualTo("y");
    }
}
package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageEnvironmentDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        MessageEnvironmentData data = new MessageEnvironmentData();

        assertThat(data.getId()).isNotNull();
        assertThat(data.getMessage()).isNotNull();
        assertThat(data.getEnvironmentType()).isNotNull();
    }

    @Test
    void parameterizedConstructor_storesValues() {
        UUID id = UUID.randomUUID();
        MessageData message = MessageData.build();
        EnvironmentType environmentType = EnvironmentType.build();
        MessageEnvironmentData data = new MessageEnvironmentData(id, message, environmentType);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getMessage()).isSameAs(message);
        assertThat(data.getEnvironmentType()).isSameAs(environmentType);
    }

    @Test
    void setters_applyDefaultsWhenNull() {
        MessageEnvironmentData data = new MessageEnvironmentData();

        data.setId(null);
        data.setMessage(null);
        data.setEnvironmentType(null);
        data.setStateData(null);

        assertThat(data.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(data.getMessage()).isNotNull();
        assertThat(data.getEnvironmentType()).isNotNull();
        assertThat(data.getStateData()).isNotNull();
    }

    @Test
    void setStateData_storesValue() {
        MessageEnvironmentData data = new MessageEnvironmentData();
        MessageEnvironmentStateData state = MessageEnvironmentStateData.build();

        data.setStateData(state);

        assertThat(data.getStateData()).isSameAs(state);
    }
}
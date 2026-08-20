package co.edu.uco.application.primaryports.dto.message;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageEnvironmentDTOTest {

    @Test
    void defaultConstructor_initializesAllFields() {
        MessageEnvironmentDTO dto = new MessageEnvironmentDTO();

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getMessageId()).isNotNull();
        assertThat(dto.getMessage()).isNotNull();
        assertThat(dto.getEnvironmentTypeId()).isNotNull();
        assertThat(dto.getStateId()).isNotNull();
    }

    @Test
    void parameterizedConstructor_setsValues() {
        UUID id = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageDTO message = MessageDTO.create("CODE", "Title", "Content", "info", "general", "app", "func");
        UUID envTypeId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();

        MessageEnvironmentDTO dto = new MessageEnvironmentDTO(id, messageId, message, envTypeId, stateId);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getMessageId()).isEqualTo(messageId);
        assertThat(dto.getMessage()).isSameAs(message);
        assertThat(dto.getEnvironmentTypeId()).isEqualTo(envTypeId);
        assertThat(dto.getStateId()).isEqualTo(stateId);
    }

    @Test
    void create_buildsInstance() {
        MessageEnvironmentDTO dto = MessageEnvironmentDTO.create(
                UUID.randomUUID(), UUID.randomUUID(),
                MessageDTO.create("CODE", "Title", "Content", "info", "general", "app", "func"),
                UUID.randomUUID(), UUID.randomUUID());

        assertThat(dto).isNotNull();
        assertThat(dto.getMessage().code()).isEqualTo("CODE");
    }
}
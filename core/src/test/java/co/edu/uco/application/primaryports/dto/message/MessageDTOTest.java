package co.edu.uco.application.primaryports.dto.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDTOTest {

    @Test
    void create_trimsAllFields() {
        MessageDTO dto = MessageDTO.create("  CODE  ", "  Title  ", "  Content  ", " info ", " general ",
                " app ", " func ");

        assertThat(dto.code()).isEqualTo("CODE");
        assertThat(dto.title()).isEqualTo("Title");
        assertThat(dto.content()).isEqualTo("Content");
        assertThat(dto.type()).isEqualTo("info");
        assertThat(dto.category()).isEqualTo("general");
        assertThat(dto.application()).isEqualTo("app");
        assertThat(dto.functionality()).isEqualTo("func");
    }

    @Test
    void create_keepsUnchangedFields() {
        MessageDTO dto = MessageDTO.create("CODE", "Title", "Content", "info", "general", "app", "func");

        assertThat(dto.code()).isEqualTo("CODE");
        assertThat(dto.title()).isEqualTo("Title");
    }

    @Test
    void equals_worksForSameValues() {
        MessageDTO a = MessageDTO.create("CODE", "Title", "Content", "info", "general", "app", "func");
        MessageDTO b = MessageDTO.create("CODE", "Title", "Content", "info", "general", "app", "func");

        assertThat(a).isEqualTo(b);
    }
}
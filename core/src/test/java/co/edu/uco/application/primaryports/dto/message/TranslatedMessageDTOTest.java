package co.edu.uco.application.primaryports.dto.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TranslatedMessageDTOTest {

    @Test
    void create_trimsStringFieldsAndMarksDynamicTranslation() {
        TranslatedMessageDTO dto = TranslatedMessageDTO.create(
                "  CODE  ", "  es  ", "  en  ", "  OrigTitle  ", "  OrigContent  ",
                "  TransTitle  ", "  TransContent  ", " info ", " general ", " app ", " func ",
                " ollama ", " llama3.2 ", 120L);

        assertThat(dto.code()).isEqualTo("CODE");
        assertThat(dto.sourceLanguage()).isEqualTo("es");
        assertThat(dto.targetLanguage()).isEqualTo("en");
        assertThat(dto.originalTitle()).isEqualTo("OrigTitle");
        assertThat(dto.originalContent()).isEqualTo("OrigContent");
        assertThat(dto.translatedTitle()).isEqualTo("TransTitle");
        assertThat(dto.translatedContent()).isEqualTo("TransContent");
        assertThat(dto.type()).isEqualTo("info");
        assertThat(dto.category()).isEqualTo("general");
        assertThat(dto.application()).isEqualTo("app");
        assertThat(dto.functionality()).isEqualTo("func");
        assertThat(dto.translationProvider()).isEqualTo("ollama");
        assertThat(dto.translationModel()).isEqualTo("llama3.2");
        assertThat(dto.translationElapsedMs()).isEqualTo(120L);
        assertThat(dto.dynamicTranslation()).isTrue();
    }

    @Test
    void canonicalConstructor_keepsDynamicTranslationFlag() {
        TranslatedMessageDTO dto = new TranslatedMessageDTO(
                "CODE", "es", "en", "T", "C", "TT", "TC", "info", "general", "app", "func",
                "ollama", "m", 10L, false);

        assertThat(dto.dynamicTranslation()).isFalse();
        assertThat(dto.translationElapsedMs()).isEqualTo(10L);
    }
}
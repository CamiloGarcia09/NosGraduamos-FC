package co.edu.uco.application.primaryports.dto.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateApplicationDTOTest {

    @Test
    void defaultConstructor_initializesAllFieldsToEmpty() {
        CreateApplicationDTO dto = new CreateApplicationDTO();

        assertAll(
                () -> assertEquals("", dto.getName()),
                () -> assertEquals("", dto.getLanguageId()),
                () -> assertEquals("", dto.getStartDate()),
                () -> assertEquals("", dto.getEndDate()),
                () -> assertEquals("", dto.getStateId()));
    }

    @Test
    void setters_trimValues() {
        CreateApplicationDTO dto = new CreateApplicationDTO();
        dto.setName("  App  ");
        dto.setLanguageId("  lang  ");
        dto.setStartDate("  2025-01-01T00:00:00  ");
        dto.setEndDate("  2025-12-31T23:59:59  ");
        dto.setStateId("  state  ");

        assertAll(
                () -> assertEquals("App", dto.getName()),
                () -> assertEquals("lang", dto.getLanguageId()),
                () -> assertEquals("2025-01-01T00:00:00", dto.getStartDate()),
                () -> assertEquals("2025-12-31T23:59:59", dto.getEndDate()),
                () -> assertEquals("state", dto.getStateId()));
    }

    @Test
    void builder_createsDtoWithValues() {
        CreateApplicationDTO dto = CreateApplicationDTO.builder()
                .name("App")
                .languageId("lang")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state")
                .build();

        assertAll(
                () -> assertEquals("App", dto.getName()),
                () -> assertEquals("lang", dto.getLanguageId()),
                () -> assertEquals("2025-01-01T00:00:00", dto.getStartDate()),
                () -> assertEquals("2025-12-31T23:59:59", dto.getEndDate()),
                () -> assertEquals("state", dto.getStateId()));
    }
}
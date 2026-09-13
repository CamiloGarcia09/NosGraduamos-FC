package co.edu.uco.application.primaryports.dto.functionality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateFunctionalityDTOTest {

    @Test
    void defaultConstructor_initializesAllFieldsToEmpty() {
        CreateFunctionalityDTO dto = new CreateFunctionalityDTO();

        assertAll(
                () -> assertEquals("", dto.getName()),
                () -> assertEquals("", dto.getApplicationId()),
                () -> assertEquals("", dto.getStartDate()),
                () -> assertEquals("", dto.getEndDate()),
                () -> assertEquals("", dto.getStateId()));
    }

    @Test
    void setters_trimValues() {
        CreateFunctionalityDTO dto = new CreateFunctionalityDTO();
        dto.setName("  Search  ");
        dto.setApplicationId("  app-1  ");
        dto.setStartDate("  2025-01-01T00:00:00  ");
        dto.setEndDate("  2025-12-31T23:59:59  ");
        dto.setStateId("  state  ");

        assertAll(
                () -> assertEquals("Search", dto.getName()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("2025-01-01T00:00:00", dto.getStartDate()),
                () -> assertEquals("2025-12-31T23:59:59", dto.getEndDate()),
                () -> assertEquals("state", dto.getStateId()));
    }

    @Test
    void builder_createsDtoWithValues() {
        CreateFunctionalityDTO dto = CreateFunctionalityDTO.builder()
                .name("Search")
                .applicationId("app-1")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state")
                .build();

        assertAll(
                () -> assertEquals("Search", dto.getName()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("2025-01-01T00:00:00", dto.getStartDate()),
                () -> assertEquals("2025-12-31T23:59:59", dto.getEndDate()),
                () -> assertEquals("state", dto.getStateId()));
    }
}
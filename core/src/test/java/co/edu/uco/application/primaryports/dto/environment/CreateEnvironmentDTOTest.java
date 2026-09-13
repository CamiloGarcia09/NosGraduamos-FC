package co.edu.uco.application.primaryports.dto.environment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateEnvironmentDTOTest {

    @Test
    void defaultConstructor_initializesAllFieldsToEmpty() {
        CreateEnvironmentDTO dto = new CreateEnvironmentDTO();

        assertAll(
                () -> assertEquals("", dto.getName()),
                () -> assertEquals("", dto.getApplicationId()),
                () -> assertEquals("", dto.getTypeId()),
                () -> assertEquals("", dto.getStateId()));
    }

    @Test
    void setters_trimValues() {
        CreateEnvironmentDTO dto = new CreateEnvironmentDTO();
        dto.setName("  Prod  ");
        dto.setApplicationId("  app-1  ");
        dto.setTypeId("  type  ");
        dto.setStateId("  state  ");

        assertAll(
                () -> assertEquals("Prod", dto.getName()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("type", dto.getTypeId()),
                () -> assertEquals("state", dto.getStateId()));
    }

    @Test
    void builder_createsDtoWithValues() {
        CreateEnvironmentDTO dto = CreateEnvironmentDTO.builder()
                .name("Prod")
                .applicationId("app-1")
                .typeId("type")
                .stateId("state")
                .build();

        assertAll(
                () -> assertEquals("Prod", dto.getName()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("type", dto.getTypeId()),
                () -> assertEquals("state", dto.getStateId()));
    }
}
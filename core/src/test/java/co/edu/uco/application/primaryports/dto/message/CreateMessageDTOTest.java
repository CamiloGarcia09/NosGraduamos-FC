package co.edu.uco.application.primaryports.dto.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateMessageDTOTest {

    @Test
    void defaultConstructor_initializesAllFieldsToEmpty() {
        CreateMessageDTO dto = new CreateMessageDTO();

        assertAll(
                () -> assertEquals("", dto.getCode()),
                () -> assertEquals("", dto.getTitle()),
                () -> assertEquals("", dto.getContent()),
                () -> assertEquals("", dto.getTypeId()),
                () -> assertEquals("", dto.getCategoryId()),
                () -> assertEquals("", dto.getStatusId()),
                () -> assertEquals("", dto.getApplicationId()),
                () -> assertEquals("", dto.getApplication()),
                () -> assertEquals("", dto.getFunctionalityId()),
                () -> assertEquals("", dto.getEnvironmentId()),
                () -> assertEquals("", dto.getMessageEnvironmentStateId()));
    }

    @Test
    void setters_trimValues() {
        CreateMessageDTO dto = new CreateMessageDTO();
        dto.setCode("  MSG-001  ");
        dto.setTitle("  A valid title  ");
        dto.setContent("  A valid content  ");
        dto.setTypeId("  type  ");
        dto.setCategoryId("  cat  ");
        dto.setStatusId("  status  ");
        dto.setApplicationId("  app-1  ");
        dto.setApplication("  App  ");
        dto.setFunctionalityId("  func-1  ");
        dto.setEnvironmentId("  env-1  ");
        dto.setMessageEnvironmentStateId("  state-1  ");

        assertAll(
                () -> assertEquals("MSG-001", dto.getCode()),
                () -> assertEquals("A valid title", dto.getTitle()),
                () -> assertEquals("A valid content", dto.getContent()),
                () -> assertEquals("type", dto.getTypeId()),
                () -> assertEquals("cat", dto.getCategoryId()),
                () -> assertEquals("status", dto.getStatusId()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("App", dto.getApplication()),
                () -> assertEquals("func-1", dto.getFunctionalityId()),
                () -> assertEquals("env-1", dto.getEnvironmentId()),
                () -> assertEquals("state-1", dto.getMessageEnvironmentStateId()));
    }

    @Test
    void builder_createsDtoWithValues() {
        CreateMessageDTO dto = CreateMessageDTO.builder()
                .code("MSG-001")
                .title("A valid title")
                .content("A valid content")
                .typeId("type")
                .categoryId("cat")
                .statusId("status")
                .applicationId("app-1")
                .application("App")
                .functionalityId("func-1")
                .environmentId("env-1")
                .messageEnvironmentStateId("state-1")
                .build();

        assertAll(
                () -> assertEquals("MSG-001", dto.getCode()),
                () -> assertEquals("A valid title", dto.getTitle()),
                () -> assertEquals("A valid content", dto.getContent()),
                () -> assertEquals("type", dto.getTypeId()),
                () -> assertEquals("cat", dto.getCategoryId()),
                () -> assertEquals("status", dto.getStatusId()),
                () -> assertEquals("app-1", dto.getApplicationId()),
                () -> assertEquals("App", dto.getApplication()),
                () -> assertEquals("func-1", dto.getFunctionalityId()),
                () -> assertEquals("env-1", dto.getEnvironmentId()),
                () -> assertEquals("state-1", dto.getMessageEnvironmentStateId()));
    }
}
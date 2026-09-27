package co.edu.uco.application.primaryports.dto.context;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ContextDTOTest {

    @Test
    void selectActiveContextDTO_trimsValuesFromSetters() {
        SelectActiveContextDTO dto = new SelectActiveContextDTO();

        dto.setOrganizationId(" organization ");
        dto.setApplicationId(" application ");
        dto.setEnvironmentId(" environment ");

        assertThat(dto).extracting(SelectActiveContextDTO::getOrganizationId,
                        SelectActiveContextDTO::getApplicationId, SelectActiveContextDTO::getEnvironmentId)
                .containsExactly("organization", "application", "environment");
    }

    @Test
    void selectActiveContextDTO_supportsBuilderAndAllArgsConstructor() {
        SelectActiveContextDTO built = SelectActiveContextDTO.builder()
                .organizationId(" org ").applicationId(" app ").environmentId(" env ").build();
        SelectActiveContextDTO constructed = new SelectActiveContextDTO(" org ", " app ", " env ");

        assertThat(built).extracting(SelectActiveContextDTO::getOrganizationId,
                        SelectActiveContextDTO::getApplicationId, SelectActiveContextDTO::getEnvironmentId)
                .containsExactly("org", "app", "env");
        assertThat(constructed).extracting(SelectActiveContextDTO::getOrganizationId,
                        SelectActiveContextDTO::getApplicationId, SelectActiveContextDTO::getEnvironmentId)
                .containsExactly("org", "app", "env");
    }

    @Test
    void activeContextDTO_exposesConfiguredValues() {
        LocalDateTime updatedAt = LocalDateTime.of(2026, 9, 22, 10, 30);

        ActiveContextDTO dto = ActiveContextDTO.builder().organizationId("org").applicationId("app")
                .environmentId("env").updatedAt(updatedAt).build();

        assertThat(dto).extracting(ActiveContextDTO::getOrganizationId, ActiveContextDTO::getApplicationId,
                        ActiveContextDTO::getEnvironmentId, ActiveContextDTO::getUpdatedAt)
                .containsExactly("org", "app", "env", updatedAt);
    }

    @Test
    void availableContextDTO_exposesCatalogItems() {
        CatalogItemDTO organization = CatalogItemDTO.create("1", "Org");
        CatalogItemDTO application = CatalogItemDTO.create("2", "App");
        CatalogItemDTO environment = CatalogItemDTO.create("3", "Env");

        AvailableContextDTO dto = new AvailableContextDTO(organization, application, environment);

        assertThat(dto).extracting(AvailableContextDTO::getOrganization, AvailableContextDTO::getApplication,
                        AvailableContextDTO::getEnvironment)
                .containsExactly(organization, application, environment);
    }

}

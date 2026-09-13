package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageEnvironmentStateData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.application.secondaryports.entity.StatusMessageData;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageCategoryCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageEnvironmentStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageTypeCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCatalogUseCaseTest {

    private static final UUID ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");

    @Mock
    private ApplicationCatalogRepository applicationCatalogRepository;
    @Mock
    private EnvironmentCatalogRepository environmentCatalogRepository;
    @Mock
    private FunctionalityCatalogRepository functionalityCatalogRepository;
    @Mock
    private MessageTypeCatalogRepository messageTypeCatalogRepository;
    @Mock
    private MessageCategoryCatalogRepository messageCategoryCatalogRepository;
    @Mock
    private MessageStateCatalogRepository messageStateCatalogRepository;
    @Mock
    private MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository;

    @Test
    void findApplications_mapsIdAndName() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(new ApplicationData(ID, "App")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findApplications();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("App"));
    }

    @Test
    void findApplications_returnsEmptyList_whenNoData() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of());
        FindCatalogUseCase useCase = buildUseCase();

        assertThat(useCase.findApplications()).isEmpty();
    }

    @Test
    void findEnvironmentsByApplication_mapsIdAndName() {
        when(environmentCatalogRepository.findAllByApplicationId("app-1"))
                .thenReturn(List.of(new EnvironmentData(ID, "Prod", new ApplicationData())));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findEnvironmentsByApplication("app-1");

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("Prod"));
    }

    @Test
    void findFunctionalitiesByApplication_mapsIdAndName() {
        when(functionalityCatalogRepository.findAllByApplicationId("app-1"))
                .thenReturn(List.of(new FunctionalityData(ID, "Search", new ApplicationData(), null, null)));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findFunctionalitiesByApplication("app-1");

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("Search"));
    }

    @Test
    void findMessageTypes_mapsIdAndName() {
        when(messageTypeCatalogRepository.findAll()).thenReturn(List.of(new MessageTypeData(ID, "TEXT")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageTypes();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("TEXT"));
    }

    @Test
    void findMessageCategories_mapsIdAndName() {
        when(messageCategoryCatalogRepository.findAll()).thenReturn(List.of(new MessageCategoryData(ID, "GENERAL")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageCategories();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("GENERAL"));
    }

    @Test
    void findMessageStates_mapsIdAndName() {
        when(messageStateCatalogRepository.findAll()).thenReturn(List.of(new StatusMessageData(ID, "ACTIVE")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageStates();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("ACTIVE"));
    }

    @Test
    void findMessageEnvironmentStates_mapsIdAndName() {
        when(messageEnvironmentStateCatalogRepository.findAll())
                .thenReturn(List.of(new MessageEnvironmentStateData(ID, "PENDING")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageEnvironmentStates();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("PENDING"));
    }

    private FindCatalogUseCase buildUseCase() {
        return new FindCatalogUseCase(
                applicationCatalogRepository,
                environmentCatalogRepository,
                functionalityCatalogRepository,
                messageTypeCatalogRepository,
                messageCategoryCatalogRepository,
                messageStateCatalogRepository,
                messageEnvironmentStateCatalogRepository);
    }
}
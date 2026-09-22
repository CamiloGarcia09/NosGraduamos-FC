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
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRule;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FindCatalogUseCaseTest {

    private static final UUID ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final UUID OTHER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175001");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", null, PrincipalType.HUMAN, Instant.MAX);

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
    @Mock
    private AuthorizationQueryPort authorizationQueryPort;
    @Mock
    private AuthorizationRule authorizationRule;

    @Test
    void findApplications_mapsIdAndName() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(new ApplicationData(ID, "App")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findApplications(null);

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).id()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).name()).isEqualTo("App"));
    }

    @Test
    void findApplications_returnsEmptyList_whenNoData() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of());
        FindCatalogUseCase useCase = buildUseCase();

        assertThat(useCase.findApplications(null)).isEmpty();
    }

    @Test
    void findEnvironmentsByApplication_mapsIdAndName() {
        when(environmentCatalogRepository.findAllByApplicationId("app-1"))
                .thenReturn(List.of(new EnvironmentData(ID, "Prod", new ApplicationData())));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findEnvironmentsByApplication("app-1", null);

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

        List<CatalogItemDTO> result = useCase.findFunctionalitiesByApplication("app-1", null);

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

    @Test
    void findApplications_filtersAuthorizedIdsAcrossOrganizations() {
        when(authorizationQueryPort.findAuthorizedApplicationIds(IDENTITY, PermissionCode.CONTEXT_SELECT))
                .thenReturn(List.of(OTHER_ID));
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(
                new ApplicationData(ID, "First organization app"),
                new ApplicationData(OTHER_ID, "Second organization app")));

        List<CatalogItemDTO> result = buildUseCase().findApplications(IDENTITY);

        assertThat(result).extracting(CatalogItemDTO::id).containsExactly(OTHER_ID.toString());
    }

    @Test
    void findApplications_preservesLegacyBehaviorWithoutIdentity() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(new ApplicationData(ID, "App")));

        assertThat(buildUseCase().findApplications(null)).hasSize(1);
        verify(authorizationQueryPort, never()).findAuthorizedApplicationIds(null, PermissionCode.CONTEXT_SELECT);
    }

    @Test
    void findEnvironmentsByApplication_allowsEnvironmentOnlyAssignmentAndFiltersResults() {
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of(OTHER_ID));
        when(environmentCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of(
                new EnvironmentData(ID, "Dev", new ApplicationData()),
                new EnvironmentData(OTHER_ID, "Prod", new ApplicationData())));

        List<CatalogItemDTO> result = buildUseCase().findEnvironmentsByApplication(ID.toString(), IDENTITY);

        assertThat(result).extracting(CatalogItemDTO::id).containsExactly(OTHER_ID.toString());
        verify(authorizationRule, never()).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    @Test
    void findEnvironmentsByApplication_deniesWhenNeitherApplicationNorEnvironmentIsAuthorized() {
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of());
        ForbiddenException denied = ForbiddenException.buildUserException("Permission denied");
        doThrow(denied).when(authorizationRule).validate(
                IDENTITY, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, ID);

        FindCatalogUseCase useCase = buildUseCase();
        assertThatThrownBy(() -> useCase
                        .findEnvironmentsByApplication(ID.toString(), IDENTITY))
                .isSameAs(denied);
    }

    @Test
    void findEnvironmentsByApplication_allowsApplicationAssignmentWhenNoEnvironmentsExist() {
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of());
        when(environmentCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of());

        List<CatalogItemDTO> result = buildUseCase().findEnvironmentsByApplication(ID.toString(), IDENTITY);

        assertThat(result).isEmpty();
        verify(authorizationRule).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    @Test
    void findFunctionalitiesByApplication_requiresApplicationPermission() {
        when(functionalityCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of());

        buildUseCase().findFunctionalitiesByApplication(ID.toString(), IDENTITY);

        verify(authorizationRule).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    private FindCatalogUseCase buildUseCase() {
        return new FindCatalogUseCase(
                applicationCatalogRepository,
                environmentCatalogRepository,
                functionalityCatalogRepository,
                messageTypeCatalogRepository,
                messageCategoryCatalogRepository,
                messageStateCatalogRepository,
                messageEnvironmentStateCatalogRepository,
                authorizationQueryPort,
                authorizationRule);
    }
}

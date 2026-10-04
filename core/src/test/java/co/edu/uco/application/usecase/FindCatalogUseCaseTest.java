package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.EnvironmentTypeData;
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
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class FindCatalogUseCaseTest {

    private static final UUID ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final UUID OTHER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175001");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", null, Instant.MAX);

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
    private AuthorizationCompositeValidator authorizationCompositeValidator;
    @Mock
    private HandlingActiveContextPort activeContextPort;
    @Mock
    private CatalogPort catalogPort;

    private void stubActiveContextForApplication() {
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(ID.toString()).build());
    }

    private static EnvironmentTypeData environmentType(final String name) {
        return new EnvironmentTypeData(UUID.randomUUID(), name);
    }

    @Test
    void findApplications_mapsIdAndName() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(new ApplicationData(ID, "App")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findApplications(null);

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("App"));
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
                .thenReturn(List.of(new EnvironmentData(ID, new ApplicationData(), environmentType("Prod"))));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findEnvironmentsByApplication("app-1", null);

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Prod"));
    }

    @Test
    void findFunctionalitiesByApplication_mapsIdAndName() {
        when(functionalityCatalogRepository.findAllByApplicationId("app-1"))
                .thenReturn(List.of(new FunctionalityData(ID, "Search", new ApplicationData())));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findFunctionalitiesByApplication("app-1", null);

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("Search"));
    }

    @Test
    void findMessageTypes_mapsIdAndName() {
        when(messageTypeCatalogRepository.findAll()).thenReturn(List.of(new MessageTypeData(ID, "TEXT")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageTypes();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("TEXT"));
    }

    @Test
    void findMessageCategories_mapsIdAndName() {
        when(messageCategoryCatalogRepository.findAll()).thenReturn(List.of(new MessageCategoryData(ID, "GENERAL")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageCategories();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("GENERAL"));
    }

    @Test
    void findMessageStates_mapsIdAndName() {
        when(messageStateCatalogRepository.findAll()).thenReturn(List.of(new StatusMessageData(ID, "ACTIVE")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageStates();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("ACTIVE"));
    }

    @Test
    void findMessageEnvironmentStates_mapsIdAndName() {
        when(messageEnvironmentStateCatalogRepository.findAll())
                .thenReturn(List.of(new MessageEnvironmentStateData(ID, "PENDING")));
        FindCatalogUseCase useCase = buildUseCase();

        List<CatalogItemDTO> result = useCase.findMessageEnvironmentStates();

        assertThat(result).hasSize(1);
        assertAll(
                () -> assertThat(result.get(0).getId()).isEqualTo(ID.toString()),
                () -> assertThat(result.get(0).getName()).isEqualTo("PENDING"));
    }

    @Test
    void findApplications_filtersAuthorizedIdsAcrossOrganizations() {
        when(authorizationQueryPort.findAuthorizedApplicationIds(IDENTITY, PermissionCode.CONTEXT_SELECT))
                .thenReturn(List.of(OTHER_ID));
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(
                new ApplicationData(ID, "First organization app"),
                new ApplicationData(OTHER_ID, "Second organization app")));

        List<CatalogItemDTO> result = buildUseCase().findApplications(IDENTITY);

        assertThat(result).extracting(CatalogItemDTO::getId).containsExactly(OTHER_ID.toString());
    }

    @Test
    void findApplications_preservesLegacyBehaviorWithoutIdentity() {
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(new ApplicationData(ID, "App")));

        assertThat(buildUseCase().findApplications(null)).hasSize(1);
        verify(authorizationQueryPort, never()).findAuthorizedApplicationIds(null, PermissionCode.CONTEXT_SELECT);
    }

    @Test
    void findEnvironmentsByApplication_allowsEnvironmentOnlyAssignmentAndFiltersResults() {
        stubActiveContextForApplication();
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of(OTHER_ID));
        when(environmentCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of(
                new EnvironmentData(ID, new ApplicationData(), environmentType("Dev")),
                new EnvironmentData(OTHER_ID, new ApplicationData(), environmentType("Prod"))));

        List<CatalogItemDTO> result = buildUseCase().findEnvironmentsByApplication(ID.toString(), IDENTITY);

        assertThat(result).extracting(CatalogItemDTO::getId).containsExactly(OTHER_ID.toString());
        verify(authorizationCompositeValidator, never()).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    @Test
    void findEnvironmentsByApplication_deniesWhenNeitherApplicationNorEnvironmentIsAuthorized() {
        stubActiveContextForApplication();
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of());
        ForbiddenException denied = ForbiddenException.buildUserException("Permission denied");
        doThrow(denied).when(authorizationCompositeValidator).validate(
                IDENTITY, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, ID);

        FindCatalogUseCase useCase = buildUseCase();
        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                        .findEnvironmentsByApplication(applicationId, IDENTITY))
                .isSameAs(denied);
    }

    @Test
    void findEnvironmentsByApplication_allowsApplicationAssignmentWhenNoEnvironmentsExist() {
        stubActiveContextForApplication();
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(IDENTITY, PermissionCode.CONTEXT_SELECT, ID))
                .thenReturn(List.of());
        when(environmentCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of());

        List<CatalogItemDTO> result = buildUseCase().findEnvironmentsByApplication(ID.toString(), IDENTITY);

        assertThat(result).isEmpty();
        verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    @Test
    void findFunctionalitiesByApplication_requiresApplicationPermission() {
        stubActiveContextForApplication();
        when(functionalityCatalogRepository.findAllByApplicationId(ID.toString())).thenReturn(List.of());

        buildUseCase().findFunctionalitiesByApplication(ID.toString(), IDENTITY);

        verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, ID);
    }

    @Test
    void findEnvironmentsByApplication_throwsForbidden_whenApplicationOutsideActiveContext() {
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(OTHER_ID.toString()).build());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");
        FindCatalogUseCase useCase = buildUseCase();

        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                .findEnvironmentsByApplication(applicationId, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(environmentCatalogRepository, authorizationQueryPort, authorizationCompositeValidator);
    }

    @Test
    void findEnvironmentsByApplication_throwsForbidden_whenActiveContextDoesNotExist() {
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");
        FindCatalogUseCase useCase = buildUseCase();

        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                .findEnvironmentsByApplication(applicationId, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(environmentCatalogRepository, authorizationQueryPort, authorizationCompositeValidator);
    }

    @Test
    void findEnvironmentsByApplication_withoutIdentitySkipsActiveContextCheck() {
        when(environmentCatalogRepository.findAllByApplicationId(ID.toString()))
                .thenReturn(List.of(new EnvironmentData(ID, new ApplicationData(), environmentType("Dev"))));

        List<CatalogItemDTO> result = buildUseCase().findEnvironmentsByApplication(ID.toString(), null);

        assertThat(result).extracting(CatalogItemDTO::getId).containsExactly(ID.toString());
        verifyNoInteractions(activeContextPort, authorizationQueryPort, authorizationCompositeValidator, catalogPort);
    }

    @Test
    void findFunctionalitiesByApplication_throwsForbidden_whenApplicationOutsideActiveContext() {
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(OTHER_ID.toString()).build());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");
        FindCatalogUseCase useCase = buildUseCase();

        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                .findFunctionalitiesByApplication(applicationId, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(functionalityCatalogRepository, authorizationCompositeValidator);
    }

    @Test
    void findFunctionalitiesByApplication_throwsForbidden_whenActiveContextDoesNotExist() {
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");
        FindCatalogUseCase useCase = buildUseCase();

        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                .findFunctionalitiesByApplication(applicationId, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(functionalityCatalogRepository, authorizationCompositeValidator);
    }

    @Test
    void findFunctionalitiesByApplication_propagatesForbiddenFromAuthorizationCompositeValidator() {
        stubActiveContextForApplication();
        ForbiddenException denied = ForbiddenException.buildUserException("Permission denied");
        doThrow(denied).when(authorizationCompositeValidator).validate(
                IDENTITY, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, ID);
        FindCatalogUseCase useCase = buildUseCase();

        String applicationId = ID.toString();
        assertThatThrownBy(() -> useCase
                .findFunctionalitiesByApplication(applicationId, IDENTITY)).isSameAs(denied);
        verifyNoInteractions(functionalityCatalogRepository);
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
                authorizationCompositeValidator,
                activeContextPort,
                catalogPort);
    }
}

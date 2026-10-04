package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.cache.ActiveContextCachePort;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.EnvironmentTypeData;
import co.edu.uco.application.secondaryports.repository.ActiveContextRepository;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.ExternalIdentityRepository;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.authorization.rule.ExternalIdentityRequiredRule;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActiveContextUseCaseTest {

    private static final UUID IDENTITY_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID ORGANIZATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    private static final UUID APPLICATION_ID = UUID.fromString("30000000-0000-0000-0000-000000000003");
    private static final UUID ENVIRONMENT_ID = UUID.fromString("40000000-0000-0000-0000-000000000004");
    private static final Instant NOW = Instant.parse("2026-09-22T14:15:16Z");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", Instant.MAX);

    @Mock private ActiveContextRepository activeContextRepository;
    @Mock private ActiveContextCachePort activeContextCachePort;
    @Mock private ExternalIdentityRepository externalIdentityRepository;
    @Mock private ApplicationCatalogRepository applicationCatalogRepository;
    @Mock private EnvironmentCatalogRepository environmentCatalogRepository;
    @Mock private AuthorizationQueryPort authorizationQueryPort;
    @Mock private AuthorizationCompositeValidator authorizationCompositeValidator;
    @Mock private ExternalIdentityRequiredRule externalIdentityRequiredRule;
    @Mock private SelectActiveContextCompositeValidator selectActiveContextValidator;
    @Mock private CatalogPort catalogPort;

    private ActiveContextUseCase useCase;
    private ExternalIdentityEntity persistedIdentity;

    @BeforeEach
    void setUp() {
        useCase = new ActiveContextUseCase(activeContextRepository, activeContextCachePort,
                externalIdentityRepository, applicationCatalogRepository, environmentCatalogRepository,
                authorizationQueryPort, authorizationCompositeValidator, externalIdentityRequiredRule,
                selectActiveContextValidator, catalogPort, Clock.fixed(NOW, ZoneOffset.UTC));
        persistedIdentity = new ExternalIdentityEntity();
        persistedIdentity.setId(IDENTITY_ID);
        persistedIdentity.setIssuer(IDENTITY.issuer());
        persistedIdentity.setSubject(IDENTITY.subject());
    }

    @Test
    void allMethods_rejectMissingIdentityBeforeUsingRepositories() {
        UnauthorizedException failure = UnauthorizedException.buildUserException("Authentication required");
        doThrow(failure).when(externalIdentityRequiredRule).validate(null);

        assertThatThrownBy(() -> useCase.findAvailableContexts(null)).isSameAs(failure);
        assertThatThrownBy(() -> useCase.findActiveContext(null)).isSameAs(failure);
        SelectActiveContextDTO selection = selection();
        assertThatThrownBy(() -> useCase.selectActiveContext(selection, null)).isSameAs(failure);
        verifyNoInteractions(activeContextRepository, activeContextCachePort, externalIdentityRepository,
                applicationCatalogRepository, environmentCatalogRepository, authorizationQueryPort,
                authorizationCompositeValidator, selectActiveContextValidator);
    }

    @Test
    void findAvailableContexts_returnsEmptyWhenNoApplicationsAreAuthorized() {
        when(authorizationQueryPort.findAuthorizedApplicationIds(IDENTITY, PermissionCode.CONTEXT_SELECT))
                .thenReturn(List.of());
        when(applicationCatalogRepository.findAll()).thenReturn(List.of(application(APPLICATION_ID, "App",
                ORGANIZATION_ID, "Org")));

        assertThat(useCase.findAvailableContexts(IDENTITY)).isEmpty();
        verifyNoInteractions(environmentCatalogRepository);
    }

    @Test
    void findAvailableContexts_filtersInvalidUnauthorizedAndCrossApplicationEntriesAndSorts() {
        UUID appBId = UUID.fromString("30000000-0000-0000-0000-000000000005");
        UUID envBId = UUID.fromString("40000000-0000-0000-0000-000000000006");
        ApplicationData appZ = application(APPLICATION_ID, "Zulu", ORGANIZATION_ID, "Beta Org");
        ApplicationData appA = application(appBId, "Alpha", ORGANIZATION_ID, "Beta Org");
        ApplicationData unauthorized = application(UUID.randomUUID(), "Hidden", UUID.randomUUID(), "Other Org");
        ApplicationData invalidOrganization = application(UUID.randomUUID(), "Invalid", DEFAULT_UUID, "");
        ApplicationData invalidApplication = application(DEFAULT_UUID, "Invalid App", ORGANIZATION_ID, "Beta Org");
        EnvironmentData envZ = environment(ENVIRONMENT_ID, "Zulu Env", appZ);
        EnvironmentData envA = environment(envBId, "Alpha Env", appA);
        EnvironmentData unauthorizedEnv = environment(UUID.randomUUID(), "Hidden Env", appZ);
        EnvironmentData crossApplication = environment(UUID.randomUUID(), "Cross Env", appA);
        EnvironmentData defaultEnvironment = environment(DEFAULT_UUID, "Default", appZ);
        when(authorizationQueryPort.findAuthorizedApplicationIds(IDENTITY, PermissionCode.CONTEXT_SELECT))
                .thenReturn(List.of(APPLICATION_ID, appBId, invalidOrganization.getId(), invalidApplication.getId()));
        when(applicationCatalogRepository.findAll())
                .thenReturn(List.of(appZ, unauthorized, invalidOrganization, invalidApplication, appA));
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(
                IDENTITY, PermissionCode.CONTEXT_SELECT, APPLICATION_ID))
                .thenReturn(List.of(ENVIRONMENT_ID, crossApplication.getId(), DEFAULT_UUID));
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(
                IDENTITY, PermissionCode.CONTEXT_SELECT, appBId)).thenReturn(List.of(envBId));
        when(environmentCatalogRepository.findAllByApplicationId(APPLICATION_ID.toString()))
                .thenReturn(List.of(envZ, unauthorizedEnv, crossApplication, defaultEnvironment));
        when(environmentCatalogRepository.findAllByApplicationId(appBId.toString())).thenReturn(List.of(envA));

        List<AvailableContextDTO> result = useCase.findAvailableContexts(IDENTITY);

        assertThat(result).extracting(context -> context.getApplication().getName())
                .containsExactly("Alpha", "Zulu");
        assertThat(result).extracting(context -> context.getEnvironment().getName())
                .containsExactly("Alpha Env", "Zulu Env");
        assertThat(result).allSatisfy(context -> {
            assertThat(context.getOrganization().getId()).isEqualTo(ORGANIZATION_ID.toString());
            assertThat(context.getEnvironment().getId()).isNotEqualTo(DEFAULT_UUID.toString());
        });
        verify(environmentCatalogRepository, never()).findAllByApplicationId(unauthorized.getId().toString());
        verify(environmentCatalogRepository, never()).findAllByApplicationId(invalidOrganization.getId().toString());
        verify(environmentCatalogRepository, never()).findAllByApplicationId(invalidApplication.getId().toString());
    }

    @Test
    void findAvailableContexts_ignoresEntriesWithMissingHierarchy_whenCatalogReturnsIncompleteData() {
        ApplicationData app = application(APPLICATION_ID, "App", ORGANIZATION_ID, "Org");
        ApplicationData withoutOrganizationIdentifier = ApplicationData.build(UUID.randomUUID(), "No Org Id");
        EnvironmentData withoutIdentifier = new EnvironmentData(null, app,
                new EnvironmentTypeData(UUID.randomUUID(), "No Id"));
        EnvironmentData validEnvironment = environment(ENVIRONMENT_ID, "Env", app);
        when(authorizationQueryPort.findAuthorizedApplicationIds(IDENTITY, PermissionCode.CONTEXT_SELECT))
                .thenReturn(List.of(APPLICATION_ID));
        when(applicationCatalogRepository.findAll())
                .thenReturn(asList(null, withoutOrganizationIdentifier, app));
        when(authorizationQueryPort.findAuthorizedEnvironmentIds(
                IDENTITY, PermissionCode.CONTEXT_SELECT, APPLICATION_ID))
                .thenReturn(List.of(ENVIRONMENT_ID));
        when(environmentCatalogRepository.findAllByApplicationId(APPLICATION_ID.toString()))
                .thenReturn(asList(null, withoutIdentifier, validEnvironment));

        List<AvailableContextDTO> result = useCase.findAvailableContexts(IDENTITY);

        assertThat(result)
                .singleElement()
                .satisfies(context -> assertThat(context.getEnvironment().getId())
                        .isEqualTo(ENVIRONMENT_ID.toString()));
    }

    @Test
    void findActiveContext_returnsMatchingCacheHitWithoutDatabaseLookupAndReauthorizes() {
        ActiveContextEntity cached = activeContext(IDENTITY_ID, ENVIRONMENT_ID);
        stubPersistedIdentity();
        when(activeContextCachePort.find(IDENTITY)).thenReturn(Optional.of(cached));

        ActiveContextDTO result = useCase.findActiveContext(IDENTITY);

        assertThat(result.getEnvironmentId()).isEqualTo(ENVIRONMENT_ID.toString());
        verify(activeContextRepository, never()).findByExternalIdentityId(any());
        verify(activeContextCachePort, never()).save(any(), any());
        verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ENVIRONMENT, ENVIRONMENT_ID);
    }

    @Test
    void findActiveContext_evictsForeignCacheEntryAndFallsBackToIdentityScopedPersistence() {
        ActiveContextEntity foreign = activeContext(UUID.randomUUID(), UUID.randomUUID());
        ActiveContextEntity persisted = activeContext(IDENTITY_ID, ENVIRONMENT_ID);
        stubPersistedIdentity();
        when(activeContextCachePort.find(IDENTITY)).thenReturn(Optional.of(foreign));
        when(activeContextRepository.findByExternalIdentityId(IDENTITY_ID)).thenReturn(Optional.of(persisted));

        ActiveContextDTO result = useCase.findActiveContext(IDENTITY);

        assertThat(result.getEnvironmentId()).isEqualTo(ENVIRONMENT_ID.toString());
        InOrder order = inOrder(activeContextCachePort, activeContextRepository);
        order.verify(activeContextCachePort).evict(IDENTITY);
        order.verify(activeContextRepository).findByExternalIdentityId(IDENTITY_ID);
        order.verify(activeContextCachePort).save(IDENTITY, persisted);
    }

    @Test
    void findActiveContext_cacheMissLoadsAndRepopulatesCache() {
        ActiveContextEntity persisted = activeContext(IDENTITY_ID, ENVIRONMENT_ID);
        stubPersistedIdentity();
        when(activeContextCachePort.find(IDENTITY)).thenReturn(Optional.empty());
        when(activeContextRepository.findByExternalIdentityId(IDENTITY_ID)).thenReturn(Optional.of(persisted));

        useCase.findActiveContext(IDENTITY);

        verify(activeContextCachePort).save(IDENTITY, persisted);
    }

    @Test
    void findActiveContext_throwsNotFoundAndDoesNotCacheWhenNoSelectionExists() {
        stubPersistedIdentity();
        when(activeContextCachePort.find(IDENTITY)).thenReturn(Optional.empty());
        when(activeContextRepository.findByExternalIdentityId(IDENTITY_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_154")).thenReturn("No active context");

        assertThatThrownBy(() -> useCase.findActiveContext(IDENTITY)).isInstanceOf(NotFoundException.class)
                .extracting("httpStatus", "userMessage").containsExactly(404, "No active context");
        verify(activeContextCachePort, never()).save(any(), any());
        verifyNoInteractions(authorizationCompositeValidator);
    }

    @Test
    void findActiveContext_propagatesRevokedPermissionAfterCacheLookup() {
        ActiveContextEntity cached = activeContext(IDENTITY_ID, ENVIRONMENT_ID);
        ForbiddenException failure = ForbiddenException.buildUserException("Permission revoked");
        stubPersistedIdentity();
        when(activeContextCachePort.find(IDENTITY)).thenReturn(Optional.of(cached));
        doThrow(failure).when(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ENVIRONMENT, ENVIRONMENT_ID);

        assertThatThrownBy(() -> useCase.findActiveContext(IDENTITY)).isSameAs(failure);
    }

    @Test
    void findActiveContext_rejectsUnpersistedIdentityWithoutReadingCache() {
        when(externalIdentityRepository.findByIssuerAndSubject(IDENTITY.issuer(), IDENTITY.subject()))
                .thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Identity forbidden");

        assertThatThrownBy(() -> useCase.findActiveContext(IDENTITY)).isInstanceOf(ForbiddenException.class)
                .extracting("httpStatus", "userMessage").containsExactly(403, "Identity forbidden");
        verifyNoInteractions(activeContextCachePort, activeContextRepository);
    }

    @Test
    void selectActiveContext_persistsExactUtcSelectionBeforeEvictingAndNeverWritesCache() {
        SelectActiveContextDTO selection = selection();
        stubPersistedIdentity();

        ActiveContextDTO result = useCase.selectActiveContext(selection, IDENTITY);

        ArgumentCaptor<ActiveContextEntity> entityCaptor = ArgumentCaptor.forClass(ActiveContextEntity.class);
        InOrder order = inOrder(selectActiveContextValidator, externalIdentityRepository, authorizationCompositeValidator,
                activeContextRepository, activeContextCachePort);
        order.verify(selectActiveContextValidator).validate(selection);
        order.verify(externalIdentityRepository).findByIssuerAndSubject(IDENTITY.issuer(), IDENTITY.subject());
        order.verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ENVIRONMENT, ENVIRONMENT_ID);
        order.verify(activeContextRepository).save(entityCaptor.capture());
        order.verify(activeContextCachePort).evict(IDENTITY);
        ActiveContextEntity saved = entityCaptor.getValue();
        assertThat(saved).extracting(ActiveContextEntity::getId, ActiveContextEntity::getExternalIdentityId,
                        ActiveContextEntity::getUpdatedAt)
                .containsExactly(IDENTITY_ID, IDENTITY_ID, LocalDateTime.of(2026, 9, 22, 14, 15, 16));
        assertThat(result.getUpdatedAt()).isEqualTo(saved.getUpdatedAt());
        verify(activeContextCachePort, never()).save(any(), any());
    }

    @Test
    void selectActiveContext_doesNotTouchCacheWhenPersistenceFails() {
        BusinessException failure = BusinessException.buildUserException("Persistence failed");
        stubPersistedIdentity();
        doThrow(failure).when(activeContextRepository).save(any());
        SelectActiveContextDTO selection = selection();

        BusinessException thrown = assertThrows(BusinessException.class,
                () -> useCase.selectActiveContext(selection, IDENTITY));

        assertSame(failure, thrown);
        verifyNoInteractions(activeContextCachePort);
    }

    @Test
    void selectActiveContext_keepsSuccessfulPersistenceWhenCacheEvictionFails() {
        stubPersistedIdentity();
        doThrow(new IllegalStateException("cache unavailable")).when(activeContextCachePort).evict(IDENTITY);

        ActiveContextDTO result = useCase.selectActiveContext(selection(), IDENTITY);

        assertThat(result.getEnvironmentId()).isEqualTo(ENVIRONMENT_ID.toString());
        verify(activeContextRepository).save(any());
    }

    @Test
    void selectActiveContext_normalizesNullContextToEmptyDtoAndSkipsDownstream_whenValidationFails() {
        BusinessRuleException failure = BusinessRuleException.buildUserException("Identificadores requeridos");
        doThrow(failure).when(selectActiveContextValidator).validate(any(SelectActiveContextDTO.class));

        BusinessRuleException thrown = assertThrows(BusinessRuleException.class,
                () -> useCase.selectActiveContext(null, IDENTITY));

        ArgumentCaptor<SelectActiveContextDTO> contextCaptor =
                ArgumentCaptor.forClass(SelectActiveContextDTO.class);
        verify(selectActiveContextValidator).validate(contextCaptor.capture());
        SelectActiveContextDTO validatedContext = contextCaptor.getValue();
        assertAll(
                () -> assertThat(validatedContext).isNotNull(),
                () -> assertThat(validatedContext.getOrganizationId()).isNull(),
                () -> assertThat(validatedContext.getApplicationId()).isNull(),
                () -> assertThat(validatedContext.getEnvironmentId()).isNull());
        assertSame(failure, thrown);
        verifyNoInteractions(externalIdentityRepository, authorizationCompositeValidator, authorizationQueryPort,
                activeContextRepository, activeContextCachePort, applicationCatalogRepository,
                environmentCatalogRepository, catalogPort);
    }

    private void stubPersistedIdentity() {
        when(externalIdentityRepository.findByIssuerAndSubject(IDENTITY.issuer(), IDENTITY.subject()))
                .thenReturn(Optional.of(persistedIdentity));
    }

    private static SelectActiveContextDTO selection() {
        return new SelectActiveContextDTO(
                ORGANIZATION_ID.toString(), APPLICATION_ID.toString(), ENVIRONMENT_ID.toString());
    }

    private static ActiveContextEntity activeContext(UUID externalIdentityId, UUID environmentId) {
        ActiveContextEntity context = new ActiveContextEntity();
        context.setId(externalIdentityId);
        context.setExternalIdentityId(externalIdentityId);
        context.setOrganizationId(ORGANIZATION_ID);
        context.setApplicationId(APPLICATION_ID);
        context.setEnvironmentId(environmentId);
        context.setUpdatedAt(LocalDateTime.of(2026, 9, 22, 12, 0));
        return context;
    }

    private static ApplicationData application(UUID id, String name, UUID organizationId, String organizationName) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(organizationId);
        organization.setName(organizationName);
        return ApplicationData.build(id, name, organization);
    }

    private static EnvironmentData environment(UUID id, String name, ApplicationData application) {
        return new EnvironmentData(id, application, new EnvironmentTypeData(UUID.randomUUID(), name));
    }
}

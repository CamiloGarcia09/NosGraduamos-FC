package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.cache.ActiveContextCachePort;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.ActiveContextRepository;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.ExternalIdentityRepository;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.authorization.rule.ExternalIdentityRequiredRule;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextCompositeValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.NotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class ActiveContextUseCase implements HandlingActiveContextPort {

    private static final Comparator<AvailableContextDTO> CONTEXT_ORDER = Comparator
            .comparing((AvailableContextDTO context) -> context.getOrganization().getName())
            .thenComparing(context -> context.getApplication().getName())
            .thenComparing(context -> context.getEnvironment().getName())
            .thenComparing(context -> context.getOrganization().getId())
            .thenComparing(context -> context.getApplication().getId())
            .thenComparing(context -> context.getEnvironment().getId());

    private final ActiveContextRepository activeContextRepository;
    private final ActiveContextCachePort activeContextCachePort;
    private final ExternalIdentityRepository externalIdentityRepository;
    private final ApplicationCatalogRepository applicationCatalogRepository;
    private final EnvironmentCatalogRepository environmentCatalogRepository;
    private final AuthorizationQueryPort authorizationQueryPort;
    private final AuthorizationCompositeValidator authorizationRule;
    private final ExternalIdentityRequiredRule externalIdentityRequiredRule;
    private final SelectActiveContextCompositeValidator selectActiveContextValidator;
    private final CatalogPort catalogPort;
    private final Clock clock;

    @SuppressWarnings("java:S107")
    public ActiveContextUseCase(final ActiveContextRepository activeContextRepository,
                                final ActiveContextCachePort activeContextCachePort,
                                final ExternalIdentityRepository externalIdentityRepository,
                                final ApplicationCatalogRepository applicationCatalogRepository,
                                final EnvironmentCatalogRepository environmentCatalogRepository,
                                final AuthorizationQueryPort authorizationQueryPort,
                                final AuthorizationCompositeValidator authorizationRule,
                                final ExternalIdentityRequiredRule externalIdentityRequiredRule,
                                final SelectActiveContextCompositeValidator selectActiveContextValidator,
                                final CatalogPort catalogPort,
                                final Clock clock) {
        this.activeContextRepository = activeContextRepository;
        this.activeContextCachePort = activeContextCachePort;
        this.externalIdentityRepository = externalIdentityRepository;
        this.applicationCatalogRepository = applicationCatalogRepository;
        this.environmentCatalogRepository = environmentCatalogRepository;
        this.authorizationQueryPort = authorizationQueryPort;
        this.authorizationRule = authorizationRule;
        this.externalIdentityRequiredRule = externalIdentityRequiredRule;
        this.selectActiveContextValidator = selectActiveContextValidator;
        this.catalogPort = catalogPort;
        this.clock = clock;
    }

    @Override
    public List<AvailableContextDTO> findAvailableContexts(final ExternalIdentity identity) {
        externalIdentityRequiredRule.validate(identity);
        Set<UUID> authorizedApplications = new HashSet<>(authorizationQueryPort.findAuthorizedApplicationIds(
                identity, PermissionCode.CONTEXT_SELECT));
        List<AvailableContextDTO> contexts = new ArrayList<>();
        applicationCatalogRepository.findAll().stream()
                .filter(this::hasValidApplicationHierarchy)
                .filter(application -> authorizedApplications.contains(application.getId()))
                .forEach(application -> addAuthorizedEnvironments(identity, application, contexts));
        return contexts.stream().sorted(CONTEXT_ORDER).toList();
    }

    @Override
    public ActiveContextDTO findActiveContext(final ExternalIdentity identity) {
        externalIdentityRequiredRule.validate(identity);
        ExternalIdentityEntity persistedIdentity = findPersistedIdentity(identity);
        ActiveContextEntity activeContext = findCachedContext(identity, persistedIdentity.getId());
        authorize(activeContext.getEnvironmentId(), identity);
        return toDTO(activeContext);
    }

    @Override
    public ActiveContextDTO selectActiveContext(final SelectActiveContextDTO context,
                                                final ExternalIdentity identity) {
        externalIdentityRequiredRule.validate(identity);
        SelectActiveContextDTO validatedContext = context == null ? new SelectActiveContextDTO() : context;
        selectActiveContextValidator.validate(validatedContext);
        SelectActiveContextDTO contextToPersist = Objects.requireNonNull(validatedContext);
        ExternalIdentityEntity persistedIdentity = findPersistedIdentity(identity);
        UUID environmentId = getUUIDFromString(contextToPersist.getEnvironmentId());
        authorize(environmentId, identity);

        ActiveContextEntity activeContext = new ActiveContextEntity();
        activeContext.setId(persistedIdentity.getId());
        activeContext.setExternalIdentityId(persistedIdentity.getId());
        activeContext.setOrganizationId(getUUIDFromString(contextToPersist.getOrganizationId()));
        activeContext.setApplicationId(getUUIDFromString(contextToPersist.getApplicationId()));
        activeContext.setEnvironmentId(environmentId);
        activeContext.setUpdatedAt(LocalDateTime.now(clock));
        activeContextRepository.save(activeContext);
        evictBestEffort(identity);
        return toDTO(activeContext);
    }

    private void addAuthorizedEnvironments(final ExternalIdentity identity, final ApplicationData application,
                                           final List<AvailableContextDTO> contexts) {
        Set<UUID> authorizedEnvironments = new HashSet<>(authorizationQueryPort.findAuthorizedEnvironmentIds(
                identity, PermissionCode.CONTEXT_SELECT, application.getId()));
        environmentCatalogRepository.findAllByApplicationId(application.getId().toString()).stream()
                .filter(environment -> hasValidEnvironment(application, environment))
                .filter(environment -> authorizedEnvironments.contains(environment.getId()))
                .map(environment -> toAvailableContext(application, environment))
                .forEach(contexts::add);
    }

    private boolean hasValidApplicationHierarchy(final ApplicationData application) {
        return application != null && isValidId(application.getId()) && application.getOrganization() != null
                && isValidId(application.getOrganization().getId());
    }

    private boolean hasValidEnvironment(final ApplicationData application, final EnvironmentData environment) {
        return environment != null && isValidId(environment.getId()) && environment.getApplication() != null
                && application.getId().equals(environment.getApplication().getId());
    }

    private boolean isValidId(final UUID id) {
        return id != null && !DEFAULT_UUID.equals(id);
    }

    private AvailableContextDTO toAvailableContext(final ApplicationData application,
                                                   final EnvironmentData environment) {
        return AvailableContextDTO.builder()
                .organization(CatalogItemDTO.create(application.getOrganization().getId().toString(),
                        application.getOrganization().getName()))
                .application(CatalogItemDTO.create(application.getId().toString(), application.getName()))
                .environment(CatalogItemDTO.create(environment.getId().toString(), environment.getType().getName()))
                .build();
    }

    private ExternalIdentityEntity findPersistedIdentity(final ExternalIdentity identity) {
        return externalIdentityRepository.findByIssuerAndSubject(identity.issuer(), identity.subject())
                .orElseThrow(() -> ForbiddenException.buildUserException(
                        catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode())));
    }

    private ActiveContextEntity findCachedContext(final ExternalIdentity identity, final UUID externalIdentityId) {
        ActiveContextEntity cached = activeContextCachePort.find(identity).orElse(null);
        if (cached != null && externalIdentityId.equals(cached.getExternalIdentityId())) {
            return cached;
        }
        if (cached != null) {
            activeContextCachePort.evict(identity);
        }
        ActiveContextEntity persisted = activeContextRepository.findByExternalIdentityId(externalIdentityId)
                .orElseThrow(() -> NotFoundException.buildUserException(
                        catalogPort.getMessage(MessageCatalogCodeEnum.FUN_154.getCode())));
        activeContextCachePort.save(identity, persisted);
        return persisted;
    }

    private void authorize(final UUID environmentId, final ExternalIdentity identity) {
        authorizationRule.validate(identity, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ENVIRONMENT, environmentId);
    }

    private void evictBestEffort(final ExternalIdentity identity) {
        try {
            activeContextCachePort.evict(identity);
        } catch (RuntimeException ignored) {
            // Persistence is authoritative; cache eviction must not roll back a successful selection.
        }
    }

    private ActiveContextDTO toDTO(final ActiveContextEntity activeContext) {
        return ActiveContextDTO.builder()
                .organizationId(activeContext.getOrganizationId().toString())
                .applicationId(activeContext.getApplicationId().toString())
                .environmentId(activeContext.getEnvironmentId().toString())
                .updatedAt(activeContext.getUpdatedAt())
                .build();
    }
}

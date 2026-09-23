package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageTypeCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageCategoryCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageEnvironmentStateCatalogRepository;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRule;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;
import static co.edu.uco.crosscutting.helpers.UtilUUID.isEqual;

public final class FindCatalogUseCase implements HandlingFindCatalogPort {

    private final ApplicationCatalogRepository applicationCatalogRepository;
    private final EnvironmentCatalogRepository environmentCatalogRepository;
    private final FunctionalityCatalogRepository functionalityCatalogRepository;
    private final MessageTypeCatalogRepository messageTypeCatalogRepository;
    private final MessageCategoryCatalogRepository messageCategoryCatalogRepository;
    private final MessageStateCatalogRepository messageStateCatalogRepository;
    private final MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository;
    private final AuthorizationQueryPort authorizationQueryPort;
    private final AuthorizationRule authorizationRule;
    private final HandlingActiveContextPort activeContextPort;
    private final CatalogPort catalogPort;

    public FindCatalogUseCase(
            ApplicationCatalogRepository applicationCatalogRepository,
            EnvironmentCatalogRepository environmentCatalogRepository,
            FunctionalityCatalogRepository functionalityCatalogRepository,
            MessageTypeCatalogRepository messageTypeCatalogRepository,
            MessageCategoryCatalogRepository messageCategoryCatalogRepository,
            MessageStateCatalogRepository messageStateCatalogRepository,
            MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository,
            AuthorizationQueryPort authorizationQueryPort,
            AuthorizationRule authorizationRule,
            HandlingActiveContextPort activeContextPort,
            CatalogPort catalogPort) {
        this.applicationCatalogRepository = applicationCatalogRepository;
        this.environmentCatalogRepository = environmentCatalogRepository;
        this.functionalityCatalogRepository = functionalityCatalogRepository;
        this.messageTypeCatalogRepository = messageTypeCatalogRepository;
        this.messageCategoryCatalogRepository = messageCategoryCatalogRepository;
        this.messageStateCatalogRepository = messageStateCatalogRepository;
        this.messageEnvironmentStateCatalogRepository = messageEnvironmentStateCatalogRepository;
        this.authorizationQueryPort = authorizationQueryPort;
        this.authorizationRule = authorizationRule;
        this.activeContextPort = activeContextPort;
        this.catalogPort = catalogPort;
    }

    @Override
    public List<CatalogItemDTO> findApplications(final ExternalIdentity identity) {
        if (identity == null) {
            return applicationCatalogRepository.findAll().stream()
                    .map(app -> CatalogItemDTO.create(app.getId().toString(), app.getName()))
                    .toList();
        }
        Set<UUID> authorizedIds = Set.copyOf(authorizationQueryPort.findAuthorizedApplicationIds(
                identity, PermissionCode.CONTEXT_SELECT));
        return applicationCatalogRepository.findAll().stream()
                .filter(application -> authorizedIds.contains(application.getId()))
                .map(app -> CatalogItemDTO.create(app.getId().toString(), app.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findEnvironmentsByApplication(final String applicationId,
                                                               final ExternalIdentity identity) {
        if (identity == null) {
            return environmentCatalogRepository.findAllByApplicationId(applicationId).stream()
                    .map(env -> CatalogItemDTO.create(env.getId().toString(), env.getName()))
                    .toList();
        }
        requireApplicationInActiveContext(identity, applicationId);
        UUID secureApplicationId = getUUIDFromString(applicationId);
        Set<UUID> authorizedIds = Set.copyOf(authorizationQueryPort.findAuthorizedEnvironmentIds(
                identity, PermissionCode.CONTEXT_SELECT, secureApplicationId));
        if (authorizedIds.isEmpty()) {
            authorizationRule.validate(identity, PermissionCode.CONTEXT_SELECT,
                    AuthorizationScopeType.APPLICATION, secureApplicationId);
        }
        return environmentCatalogRepository.findAllByApplicationId(applicationId).stream()
                .filter(environment -> authorizedIds.contains(environment.getId()))
                .map(env -> CatalogItemDTO.create(env.getId().toString(), env.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findFunctionalitiesByApplication(final String applicationId,
                                                                  final ExternalIdentity identity) {
        if (identity != null) {
            requireApplicationInActiveContext(identity, applicationId);
            authorizationRule.validate(identity, PermissionCode.CONTEXT_SELECT,
                    AuthorizationScopeType.APPLICATION, getUUIDFromString(applicationId));
        }
        return functionalityCatalogRepository.findAllByApplicationId(applicationId).stream()
                .map(func -> CatalogItemDTO.create(func.getId().toString(), func.getName()))
                .toList();
    }

    private void requireApplicationInActiveContext(final ExternalIdentity identity, final String applicationId) {
        var activeContext = activeContextPort.findActiveContext(identity);
        if (activeContext == null || !isEqual(getUUIDFromString(applicationId),
                getUUIDFromString(activeContext.getApplicationId()))) {
            throw ForbiddenException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()));
        }
    }

    @Override
    public List<CatalogItemDTO> findMessageTypes() {
        return messageTypeCatalogRepository.findAll().stream()
                .map(type -> CatalogItemDTO.create(type.getId().toString(), type.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageCategories() {
        return messageCategoryCatalogRepository.findAll().stream()
                .map(cat -> CatalogItemDTO.create(cat.getId().toString(), cat.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageStates() {
        return messageStateCatalogRepository.findAll().stream()
                .map(st -> CatalogItemDTO.create(st.getId().toString(), st.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageEnvironmentStates() {
        return messageEnvironmentStateCatalogRepository.findAll().stream()
                .map(st -> CatalogItemDTO.create(st.getId().toString(), st.getName()))
                .toList();
    }
}

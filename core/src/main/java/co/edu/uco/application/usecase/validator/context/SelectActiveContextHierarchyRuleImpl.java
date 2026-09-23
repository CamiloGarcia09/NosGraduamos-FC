package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ConflictException;
import co.edu.uco.crosscutting.exceptions.NotFoundException;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class SelectActiveContextHierarchyRuleImpl implements SelectActiveContextHierarchyRule {

    private final OrganizationRepository organizationRepository;
    private final ApplicationRepository applicationRepository;
    private final EnvironmentRepository environmentRepository;
    private final CatalogPort catalogPort;

    public SelectActiveContextHierarchyRuleImpl(final OrganizationRepository organizationRepository,
                                                final ApplicationRepository applicationRepository,
                                                final EnvironmentRepository environmentRepository,
                                                final CatalogPort catalogPort) {
        this.organizationRepository = organizationRepository;
        this.applicationRepository = applicationRepository;
        this.environmentRepository = environmentRepository;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final SelectActiveContextDTO context) {
        UUID organizationId = getUUIDFromString(context.getOrganizationId());
        if (organizationRepository.findById(organizationId).isEmpty()) {
            throw notFound(MessageCatalogCodeEnum.FUN_156);
        }
        ApplicationData application = applicationRepository.findById(context.getApplicationId())
                .orElseThrow(() -> notFound(MessageCatalogCodeEnum.FUN_157));
        EnvironmentData environment = environmentRepository.findById(context.getEnvironmentId())
                .orElseThrow(() -> notFound(MessageCatalogCodeEnum.FUN_158));
        if (!organizationId.equals(application.getOrganization().getId())) {
            throw conflict(MessageCatalogCodeEnum.FUN_159);
        }
        if (!application.getId().equals(environment.getApplication().getId())) {
            throw conflict(MessageCatalogCodeEnum.FUN_160);
        }
    }

    private NotFoundException notFound(final MessageCatalogCodeEnum code) {
        return NotFoundException.buildUserException(catalogPort.getMessage(code.getCode()));
    }

    private ConflictException conflict(final MessageCatalogCodeEnum code) {
        return ConflictException.buildUserException(catalogPort.getMessage(code.getCode()));
    }
}

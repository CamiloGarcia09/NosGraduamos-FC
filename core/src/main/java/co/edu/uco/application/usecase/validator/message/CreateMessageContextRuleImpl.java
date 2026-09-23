package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;

import java.util.List;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getStringFromUUID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;
import static co.edu.uco.crosscutting.helpers.UtilUUID.isEqual;

public final class CreateMessageContextRuleImpl implements CreateMessageContextRule {

    private final EnvironmentRepository environmentRepository;
    private final FunctionalityCatalogRepository functionalityCatalogRepository;
    private final CatalogPort catalogPort;

    public CreateMessageContextRuleImpl(EnvironmentRepository environmentRepository,
                                        FunctionalityCatalogRepository functionalityCatalogRepository,
                                        CatalogPort catalogPort) {
        this.environmentRepository = environmentRepository;
        this.functionalityCatalogRepository = functionalityCatalogRepository;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(CreateMessageDTO dto, String authenticatedEnvironmentId) {
        UUID environmentId = validateAuthenticatedEnvironment(dto.getEnvironmentId(), authenticatedEnvironmentId);
        UUID applicationId = getUUIDFromString(dto.getApplicationId());
        UUID functionalityId = getUUIDFromString(dto.getFunctionalityId());

        var environment = environmentRepository.findById(getStringFromUUID(environmentId))
                .orElseThrow(() -> forbidden(MessageCatalogCodeEnum.FUN_035));
        if (!isEqual(applicationId, environment.getApplication().getId())) {
            throw forbidden(MessageCatalogCodeEnum.FUN_036);
        }

        List<FunctionalityData> functionalities =
                functionalityCatalogRepository.findAllByApplicationId(getStringFromUUID(applicationId));
        if (isNullObject(functionalities) || functionalities.stream().noneMatch(functionality ->
                belongsToApplication(functionality, functionalityId, applicationId))) {
            throw forbidden(MessageCatalogCodeEnum.FUN_146);
        }
    }

    private UUID validateAuthenticatedEnvironment(String requestedEnvironmentId, String authenticatedEnvironmentId) {
        if (isEmptyOrNull(authenticatedEnvironmentId)) {
            throw forbidden(MessageCatalogCodeEnum.FUN_145);
        }
        UUID environmentId = getUUIDFromString(authenticatedEnvironmentId);
        if (!isEmptyOrNull(requestedEnvironmentId)
                && !isEqual(environmentId, getUUIDFromString(requestedEnvironmentId))) {
            throw forbidden(MessageCatalogCodeEnum.FUN_145);
        }
        return environmentId;
    }

    private boolean belongsToApplication(FunctionalityData functionality, UUID functionalityId,
                                         UUID applicationId) {
        return !isNullObject(functionality)
                && isEqual(functionalityId, functionality.getId())
                && !isNullObject(functionality.getApplication())
                && isEqual(applicationId, functionality.getApplication().getId());
    }

    private ForbiddenException forbidden(MessageCatalogCodeEnum code) {
        return ForbiddenException.buildUserException(catalogPort.getMessage(code.getCode()));
    }
}

package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.EnvironmentTypeData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.handling.HandlingCreateEnvironmentPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.environment.CreateEnvironmentCompositeValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;
import static co.edu.uco.crosscutting.helpers.UtilUUID.isEqual;

@Component
public final class CreateEnvironmentUseCase implements HandlingCreateEnvironmentPort {

    private final EnvironmentRepository environmentRepository;
    private final CreateEnvironmentCompositeValidator validator;
    private final HandlingActiveContextPort activeContextPort;
    private final AuthorizationCompositeValidator authorizationRule;
    private final CatalogPort catalogPort;
    private final LoggingPort log;

    public CreateEnvironmentUseCase(EnvironmentRepository environmentRepository,
                                    CreateEnvironmentCompositeValidator validator,
                                    HandlingActiveContextPort activeContextPort,
                                     AuthorizationCompositeValidator authorizationRule,
                                    CatalogPort catalogPort,
                                    LoggingPortFactory loggerFactory) {
        this.environmentRepository = environmentRepository;
        this.validator = validator;
        this.activeContextPort = activeContextPort;
        this.authorizationRule = authorizationRule;
        this.catalogPort = catalogPort;
        this.log = loggerFactory.getLogger(CreateEnvironmentUseCase.class);
    }

    @Override
    public void createEnvironment(CreateEnvironmentDTO dto, ExternalIdentity identity) {
        authorizeAgainstActiveContext(identity, dto.getApplicationId());
        validator.validate(dto);

        try {
            var application = ApplicationData.build(UtilUUID.getStringToUUID(dto.getApplicationId()), "");
            var type = new EnvironmentTypeData(UtilUUID.getStringToUUID(dto.getTypeId()), "");
            var environment = new EnvironmentData(UtilUUID.getNewUUID(), application, type);
            environmentRepository.create(environment, dto.getTypeId(), dto.getStateId());
            log.info("Environment created successfully with type id: {}", dto.getTypeId());
        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating environment in repository", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al crear el entorno", ex, ExceptionLocation.APPLICATION);
        }
    }

    private void authorizeAgainstActiveContext(final ExternalIdentity identity, final String requestedApplicationId) {
        if (identity == null) {
            return;
        }
        var activeContext = activeContextPort.findActiveContext(identity);
        if (activeContext == null) {
            throw forbidden();
        }
        var contextApplicationId = getUUIDFromString(activeContext.getApplicationId());
        if (!isEqual(contextApplicationId, getUUIDFromString(requestedApplicationId))) {
            throw forbidden();
        }
        authorizationRule.validate(identity, PermissionCode.ENVIRONMENT_CREATE,
                AuthorizationScopeType.APPLICATION, contextApplicationId);
    }

    private ForbiddenException forbidden() {
        return ForbiddenException.buildUserException(
                catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()));
    }
}

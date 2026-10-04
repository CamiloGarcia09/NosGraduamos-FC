package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.handling.HandlingCreateFunctionalityPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.functionality.CreateFunctionalityCompositeValidator;
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
public final class CreateFunctionalityUseCase implements HandlingCreateFunctionalityPort {

    private final FunctionalityRepository functionalityRepository;
    private final CreateFunctionalityCompositeValidator validator;
    private final HandlingActiveContextPort activeContextPort;
    private final AuthorizationCompositeValidator authorizationRule;
    private final CatalogPort catalogPort;
    private final LoggingPort log;

    public CreateFunctionalityUseCase(FunctionalityRepository functionalityRepository,
                                      CreateFunctionalityCompositeValidator validator,
                                      HandlingActiveContextPort activeContextPort,
                                       AuthorizationCompositeValidator authorizationRule,
                                      CatalogPort catalogPort,
                                      LoggingPortFactory loggerFactory) {
        this.functionalityRepository = functionalityRepository;
        this.validator = validator;
        this.activeContextPort = activeContextPort;
        this.authorizationRule = authorizationRule;
        this.catalogPort = catalogPort;
        this.log = loggerFactory.getLogger(CreateFunctionalityUseCase.class);
    }

    @Override
    public void createFunctionality(CreateFunctionalityDTO dto, ExternalIdentity identity) {
        authorizeAgainstActiveContext(identity, dto.getApplicationId());
        validator.validate(dto);

        try {
            var application = ApplicationData.build(UtilUUID.getStringToUUID(dto.getApplicationId()), "");
            var functionality = new FunctionalityData(
                    UtilUUID.getNewUUID(),
                    dto.getName(),
                    application
            );
            functionalityRepository.create(functionality, dto.getStateId());
            log.info("Functionality created successfully with name: {}", dto.getName());
        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating functionality in repository", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al crear la funcionalidad", ex, ExceptionLocation.APPLICATION);
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
        authorizationRule.validate(identity, PermissionCode.FUNCTIONALITY_CREATE,
                AuthorizationScopeType.APPLICATION, contextApplicationId);
    }

    private ForbiddenException forbidden() {
        return ForbiddenException.buildUserException(
                catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()));
    }
}

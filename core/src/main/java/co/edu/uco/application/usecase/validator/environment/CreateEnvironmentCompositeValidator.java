package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.CompositeValidatorSupport;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public final class CreateEnvironmentCompositeValidator extends CompositeValidatorSupport {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre del entorno es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre del entorno no puede superar los 50 caracteres.";
    private static final String MESSAGE_APPLICATION_REQUIRED = "El id de la aplicación es requerido.";
    private static final String MESSAGE_APPLICATION_NOT_EXISTS = "La aplicación a la que se asocia el entorno no existe.";
    private static final String MESSAGE_TYPE_REQUIRED = "El tipo del entorno es requerido.";
    private static final String MESSAGE_TYPE_NOT_EXISTS = "El tipo de entorno no existe.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado del entorno es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado del entorno no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe un entorno con el mismo nombre para la aplicación.";

    private final EnvironmentRepository environmentRepository;
    private final UUIDValidator uuidValidator;

    public CreateEnvironmentCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository,
                                               EnvironmentRepository environmentRepository,
                                               UUIDValidator uuidValidator) {
        super(catalogPort, recordExistsCatalogPort, applicationRepository);
        this.environmentRepository = environmentRepository;
        this.uuidValidator = uuidValidator;
    }

    public void validate(CreateEnvironmentDTO dto) {
        validateNotNull(dto);

        validateRequiredName(dto.getName(), MESSAGE_NAME_REQUIRED, MESSAGE_NAME_MAX_LENGTH);

        validateRequiredAndExistingApplicationId(dto.getApplicationId(), uuidValidator,
                MESSAGE_APPLICATION_REQUIRED, MESSAGE_APPLICATION_NOT_EXISTS);

        validateCatalogReference(dto.getTypeId(), ReferenceCatalog.ENVIRONMENT_TYPE,
                MESSAGE_TYPE_REQUIRED, MESSAGE_TYPE_NOT_EXISTS);

        validateCatalogReference(dto.getStateId(), ReferenceCatalog.ENVIRONMENT_STATE,
                MESSAGE_STATE_REQUIRED, MESSAGE_STATE_NOT_EXISTS);

        if (environmentRepository.existsByNameAndApplicationId(dto.getName(), dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
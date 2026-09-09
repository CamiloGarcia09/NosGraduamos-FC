package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

@Component
public final class CreateEnvironmentCompositeValidator {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre del entorno es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre del entorno no puede superar los 50 caracteres.";
    private static final String MESSAGE_APPLICATION_REQUIRED = "El id de la aplicación es requerido.";
    private static final String MESSAGE_APPLICATION_NOT_EXISTS = "La aplicación a la que se asocia el entorno no existe.";
    private static final String MESSAGE_TYPE_REQUIRED = "El tipo del entorno es requerido.";
    private static final String MESSAGE_TYPE_NOT_EXISTS = "El tipo de entorno no existe.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado del entorno es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado del entorno no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe un entorno con el mismo nombre para la aplicación.";

    private final CatalogPort catalogPort;
    private final RecordExistsCatalogPort recordExistsCatalogPort;
    private final ApplicationRepository applicationRepository;
    private final EnvironmentRepository environmentRepository;
    private final UUIDValidator uuidValidator;

    public CreateEnvironmentCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository,
                                               EnvironmentRepository environmentRepository,
                                               UUIDValidator uuidValidator) {
        this.catalogPort = catalogPort;
        this.recordExistsCatalogPort = recordExistsCatalogPort;
        this.applicationRepository = applicationRepository;
        this.environmentRepository = environmentRepository;
        this.uuidValidator = uuidValidator;
    }

    public void validate(CreateEnvironmentDTO dto) {
        if (isNullObject(dto)) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())
            );
        }

        if (isEmptyOrNull(dto.getName())) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_REQUIRED);
        }
        if (dto.getName().trim().length() > 50) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_MAX_LENGTH);
        }

        if (isEmptyOrNull(dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_APPLICATION_REQUIRED);
        }
        uuidValidator.validate(dto.getApplicationId());
        if (!applicationRepository.existsById(dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_APPLICATION_NOT_EXISTS);
        }

        if (isEmptyOrNull(dto.getTypeId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_TYPE_REQUIRED);
        }
        if (!recordExistsCatalogPort.exists(ReferenceCatalog.ENVIRONMENT_TYPE, dto.getTypeId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_TYPE_NOT_EXISTS);
        }

        if (isEmptyOrNull(dto.getStateId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_STATE_REQUIRED);
        }
        if (!recordExistsCatalogPort.exists(ReferenceCatalog.ENVIRONMENT_STATE, dto.getStateId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_STATE_NOT_EXISTS);
        }

        if (environmentRepository.existsByNameAndApplicationId(dto.getName(), dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
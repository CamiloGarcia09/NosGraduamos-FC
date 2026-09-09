package co.edu.uco.application.usecase.validator.functionality;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.application.usecase.validator.impl.ValidDateRangeValidator;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;
import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

@Component
public final class CreateFunctionalityCompositeValidator {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre de la funcionalidad es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre de la funcionalidad no puede superar los 50 caracteres.";
    private static final String MESSAGE_APPLICATION_REQUIRED = "El id de la aplicación es requerido.";
    private static final String MESSAGE_APPLICATION_NOT_EXISTS = "La aplicación a la que se asocia la funcionalidad no existe.";
    private static final String MESSAGE_START_DATE_REQUIRED = "La fecha de inicio de la funcionalidad es requerida.";
    private static final String MESSAGE_END_DATE_REQUIRED = "La fecha de fin de la funcionalidad es requerida.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado de la funcionalidad es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado de la funcionalidad no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe una funcionalidad con el mismo nombre para la aplicación.";

    private final CatalogPort catalogPort;
    private final RecordExistsCatalogPort recordExistsCatalogPort;
    private final ApplicationRepository applicationRepository;
    private final FunctionalityRepository functionalityRepository;
    private final UUIDValidator uuidValidator;
    private final DateValidValidator dateValidValidator;

    public CreateFunctionalityCompositeValidator(CatalogPort catalogPort,
                                                 RecordExistsCatalogPort recordExistsCatalogPort,
                                                 ApplicationRepository applicationRepository,
                                                 FunctionalityRepository functionalityRepository,
                                                 UUIDValidator uuidValidator,
                                                 DateValidValidator dateValidValidator) {
        this.catalogPort = catalogPort;
        this.recordExistsCatalogPort = recordExistsCatalogPort;
        this.applicationRepository = applicationRepository;
        this.functionalityRepository = functionalityRepository;
        this.uuidValidator = uuidValidator;
        this.dateValidValidator = dateValidValidator;
    }

    public void validate(CreateFunctionalityDTO dto) {
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

        if (isEmptyOrNull(dto.getStartDate())) {
            throw BusinessRuleException.buildUserException(MESSAGE_START_DATE_REQUIRED);
        }
        dateValidValidator.validate(dto.getStartDate());
        LocalDateTime startDate = parseDate(dto.getStartDate());

        if (isEmptyOrNull(dto.getEndDate())) {
            throw BusinessRuleException.buildUserException(MESSAGE_END_DATE_REQUIRED);
        }
        dateValidValidator.validate(dto.getEndDate());
        LocalDateTime endDate = parseDate(dto.getEndDate());

        ValidDateRangeValidator.validate(startDate, endDate);

        if (isEmptyOrNull(dto.getStateId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_STATE_REQUIRED);
        }
        if (!recordExistsCatalogPort.exists(ReferenceCatalog.FUNCTIONALITY_STATE, dto.getStateId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_STATE_NOT_EXISTS);
        }

        if (functionalityRepository.existsByNameAndApplicationId(dto.getName(), dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
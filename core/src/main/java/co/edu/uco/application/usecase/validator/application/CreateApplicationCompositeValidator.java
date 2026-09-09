package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
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
public final class CreateApplicationCompositeValidator {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre de la aplicación es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre de la aplicación no puede superar los 50 caracteres.";
    private static final String MESSAGE_LANGUAGE_REQUIRED = "El idioma de la aplicación es requerido.";
    private static final String MESSAGE_LANGUAGE_NOT_EXISTS = "El idioma de la aplicación no existe.";
    private static final String MESSAGE_START_DATE_REQUIRED = "La fecha de inicio de la aplicación es requerida.";
    private static final String MESSAGE_END_DATE_REQUIRED = "La fecha de fin de la aplicación es requerida.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado de la aplicación es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado de la aplicación no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe una aplicación con el nombre proporcionado.";

    private final CatalogPort catalogPort;
    private final RecordExistsCatalogPort recordExistsCatalogPort;
    private final ApplicationRepository applicationRepository;
    private final DateValidValidator dateValidValidator;

    public CreateApplicationCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository,
                                               DateValidValidator dateValidValidator) {
        this.catalogPort = catalogPort;
        this.recordExistsCatalogPort = recordExistsCatalogPort;
        this.applicationRepository = applicationRepository;
        this.dateValidValidator = dateValidValidator;
    }

    public void validate(CreateApplicationDTO dto) {
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

        if (isEmptyOrNull(dto.getLanguageId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_LANGUAGE_REQUIRED);
        }
        if (!recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, dto.getLanguageId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_LANGUAGE_NOT_EXISTS);
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
        if (!recordExistsCatalogPort.exists(ReferenceCatalog.APPLICATION_STATE, dto.getStateId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_STATE_NOT_EXISTS);
        }

        if (applicationRepository.findByName(dto.getName()).isPresent()) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
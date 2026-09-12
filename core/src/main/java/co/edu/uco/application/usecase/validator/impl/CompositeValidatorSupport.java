package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.time.LocalDateTime;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;
import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

public abstract class CompositeValidatorSupport {

    private static final int NAME_MAX_LENGTH = 50;

    protected final CatalogPort catalogPort;
    protected final RecordExistsCatalogPort recordExistsCatalogPort;
    protected final ApplicationRepository applicationRepository;

    protected CompositeValidatorSupport(CatalogPort catalogPort,
                                        RecordExistsCatalogPort recordExistsCatalogPort,
                                        ApplicationRepository applicationRepository) {
        this.catalogPort = catalogPort;
        this.recordExistsCatalogPort = recordExistsCatalogPort;
        this.applicationRepository = applicationRepository;
    }

    protected void validateNotNull(Object dto) {
        if (isNullObject(dto)) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())
            );
        }
    }

    protected void validateRequiredName(String name, String requiredMessage, String maxLengthMessage) {
        if (isEmptyOrNull(name)) {
            throw BusinessRuleException.buildUserException(requiredMessage);
        }
        if (name.trim().length() > NAME_MAX_LENGTH) {
            throw BusinessRuleException.buildUserException(maxLengthMessage);
        }
    }

    protected void validateRequiredAndExistingApplicationId(String applicationId, UUIDValidator uuidValidator,
                                                            String requiredMessage, String notExistsMessage) {
        if (isEmptyOrNull(applicationId)) {
            throw BusinessRuleException.buildUserException(requiredMessage);
        }
        uuidValidator.validate(applicationId);
        if (!applicationRepository.existsById(applicationId)) {
            throw BusinessRuleException.buildUserException(notExistsMessage);
        }
    }

    protected void validateDateRange(String startDate, String endDate, String requiredStartMessage,
                                     String requiredEndMessage, DateValidValidator dateValidValidator) {
        if (isEmptyOrNull(startDate)) {
            throw BusinessRuleException.buildUserException(requiredStartMessage);
        }
        dateValidValidator.validate(startDate);
        LocalDateTime start = parseDate(startDate);

        if (isEmptyOrNull(endDate)) {
            throw BusinessRuleException.buildUserException(requiredEndMessage);
        }
        dateValidValidator.validate(endDate);
        LocalDateTime end = parseDate(endDate);

        ValidDateRangeValidator.validate(start, end);
    }

    protected void validateCatalogReference(String id, ReferenceCatalog catalog, String requiredMessage,
                                            String notExistsMessage) {
        if (isEmptyOrNull(id)) {
            throw BusinessRuleException.buildUserException(requiredMessage);
        }
        if (!recordExistsCatalogPort.exists(catalog, id)) {
            throw BusinessRuleException.buildUserException(notExistsMessage);
        }
    }
}
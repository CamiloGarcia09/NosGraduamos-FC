package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.time.LocalDateTime;

/**
 * Valida que la fecha de inicio no sea posterior a la fecha de fin.
 */
public final class ValidDateRangeValidator {

    private static final String MESSAGE_START_AFTER_END = "La fecha de inicio no puede ser posterior a la fecha de fin.";

    private ValidDateRangeValidator() {
    }

    public static void validate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw BusinessRuleException.buildUserException(MESSAGE_START_AFTER_END);
        }
    }
}
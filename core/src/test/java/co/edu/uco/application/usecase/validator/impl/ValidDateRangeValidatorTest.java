package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ValidDateRangeValidatorTest {

    private static final LocalDateTime START = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

    @Test
    void validate_acceptsStartDateBeforeEndDate() {
        assertDoesNotThrow(() -> ValidDateRangeValidator.validate(START, END));
    }

    @Test
    void validate_acceptsEqualDates() {
        assertDoesNotThrow(() -> ValidDateRangeValidator.validate(START, START));
    }

    @Test
    void validate_throwsBusinessRule_whenStartDateIsAfterEndDate() {
        assertThatThrownBy(() -> ValidDateRangeValidator.validate(END, START))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de inicio no puede ser posterior a la fecha de fin."));
    }
}
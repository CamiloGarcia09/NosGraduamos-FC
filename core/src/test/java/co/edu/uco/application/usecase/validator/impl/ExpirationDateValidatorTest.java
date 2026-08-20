package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpirationDateValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private ExpirationDateValidator validator;

    @Test
    void validate_doesNotThrow_forFutureDate() {
        assertThatCode(() -> validator.validate(LocalDateTime.now().plusDays(1)))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenDateIsInThePast() {
        when(catalogPort.getMessage("FUN_037")).thenReturn("Expiration date is in the past");

        assertThatThrownBy(() -> validator.validate(LocalDateTime.now().minusDays(1)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Expiration date is in the past"));
    }
}
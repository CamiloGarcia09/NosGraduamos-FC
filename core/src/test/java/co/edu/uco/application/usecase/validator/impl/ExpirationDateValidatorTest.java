package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpirationDateValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    private ExpirationDateValidator validator;

    private static final LocalDateTime NOW_UTC = LocalDateTime.of(2025, 1, 15, 15, 30, 45);

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-15T15:30:45Z"), ZoneOffset.ofHours(-5));
        validator = new ExpirationDateValidator(catalogPort, clock);
    }

    @Test
    void validate_doesNotThrow_forFutureDate() {
        assertThatCode(() -> validator.validate(NOW_UTC.plusDays(1)))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenDateEqualsCurrentUtcTime() {
        assertThatCode(() -> validator.validate(NOW_UTC))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenDateIsInThePast() {
        when(catalogPort.getMessage("FUN_037")).thenReturn("Expiration date is in the past");

        LocalDateTime pastDate = NOW_UTC.minusNanos(1);
        assertThatThrownBy(() -> validator.validate(pastDate))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Expiration date is in the past"));
    }
}

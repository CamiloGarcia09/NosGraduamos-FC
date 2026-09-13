package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DateValidValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private DateValidValidator validator;

    @Test
    void validate_doesNotThrow_forValidDateTime() {
        assertThatCode(() -> validator.validate("2025-01-01T10:30:00")).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_forInvalidCharacters() {
        when(catalogPort.getMessage("FUN_039")).thenReturn("Date has invalid format");

        assertThatThrownBy(() -> validator.validate("invalid date!"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Date has invalid format"));
    }

    @Test
    void validate_throwsBusinessRuleException_whenDateCannotBeParsed() {
        assertThatThrownBy(() -> validator.validate("2025-99-99T99:99:99"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("The date to be converted has no valid format."));
    }
}
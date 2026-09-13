package co.edu.uco.application.usecase.validator.page;

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
class PageRequestRangeValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private PageRequestRangeValidator validator;

    @Test
    void validate_doesNotThrow_whenPageWithinRange() {
        assertThatCode(() -> validator.validate(2, 5)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenPageEqualsTotalPages() {
        assertThatCode(() -> validator.validate(5, 5)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenPageExceedsTotalPages() {
        when(catalogPort.getMessage("FUN_028")).thenReturn("Requested page exceeds total pages: %d");

        assertThatThrownBy(() -> validator.validate(6, 5))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Requested page exceeds total pages: 5"));
    }
}
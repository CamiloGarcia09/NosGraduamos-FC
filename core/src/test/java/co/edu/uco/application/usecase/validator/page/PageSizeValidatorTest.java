package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageSizeValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @Test
    void validate_doesNotThrow_forSizeWithinRange() {
        PageSizeValidator validator = new PageSizeValidator(catalogPort);
        SimplePageRequest request = new SimplePageRequest();
        request.setSize(50);

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_forMinimumSize() {
        PageSizeValidator validator = new PageSizeValidator(catalogPort);
        SimplePageRequest request = new SimplePageRequest();
        request.setSize(1);

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_forMaximumSize() {
        PageSizeValidator validator = new PageSizeValidator(catalogPort);
        SimplePageRequest request = new SimplePageRequest();
        request.setSize(100);

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenSizeExceedsMaximum() {
        when(catalogPort.getMessage("FUN_029")).thenReturn("Page size must be between 1 and %d");
        PageSizeValidator validator = new PageSizeValidator(catalogPort);
        SimplePageRequest request = new SimplePageRequest();
        request.setSize(101);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Page size must be between 1 and 100"));
    }

    @Test
    void validate_throwsBusinessRuleException_whenSizeIsZero() {
        when(catalogPort.getMessage("FUN_029")).thenReturn("Page size must be between 1 and %d");
        PageSizeValidator validator = new PageSizeValidator(catalogPort);
        SimplePageRequest request = new SimplePageRequest();
        request.setSize(0);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class);
    }
}
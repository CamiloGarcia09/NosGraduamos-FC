package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
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
class PageNumberValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private PageNumberValidator validator;

    @Test
    void validate_doesNotThrow_whenPageIsOne() {
        SimplePageRequest request = new SimplePageRequest();
        request.setPage(1);
        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenPageIsGreaterThanOne() {
        SimplePageRequest request = new SimplePageRequest();
        request.setPage(3);
        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenPageIsZero() {
        when(catalogPort.getMessage("FUN_032")).thenReturn("Page number must be greater than or equal to 1");
        SimplePageRequest request = new SimplePageRequest();
        request.setPage(0);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Page number must be greater than or equal to 1"));
    }
}
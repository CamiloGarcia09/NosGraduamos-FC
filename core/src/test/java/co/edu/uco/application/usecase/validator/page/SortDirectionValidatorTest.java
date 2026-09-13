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
class SortDirectionValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private SortDirectionValidator validator;

    @Test
    void validate_doesNotThrow_forAscending() {
        SimplePageRequest request = new SimplePageRequest();
        request.setSort("ASC");
        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_forDescendingIgnoringCase() {
        SimplePageRequest request = new SimplePageRequest();
        request.setSort("desc");
        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_forInvalidSort() {
        when(catalogPort.getMessage("FUN_031")).thenReturn("Invalid sort direction");
        SimplePageRequest request = new SimplePageRequest();
        request.setSort("SIDEWAYS");

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid sort direction"));
    }
}
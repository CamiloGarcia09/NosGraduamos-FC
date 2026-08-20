package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SortColumnValidatorTest {

    @BeforeEach
    void setUp() {
        CatalogPort catalogPort = mock(CatalogPort.class);
        when(catalogPort.getMessage(org.mockito.ArgumentMatchers.anyString())).thenReturn("mensaje");
        when(catalogPort.getTitle(org.mockito.ArgumentMatchers.anyString())).thenReturn("titulo");
        CatalogPortStaticRef.set(catalogPort);
    }

    @AfterEach
    void tearDown() {
        CatalogPortStaticRef.set(null);
    }

    @Test
    void getInstance_returnsSameInstanceForSameClass() {
        SortColumnValidator first = SortColumnValidator.getInstance(SimplePageRequest.class);
        SortColumnValidator second = SortColumnValidator.getInstance(SimplePageRequest.class);
        assertThat(first).isSameAs(second);
    }

    @Test
    void validate_doesNotThrow_whenColumnIsValid() {
        SortColumnValidator validator = SortColumnValidator.getInstance(SimplePageRequest.class);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("page");

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenColumnIsNotInModel() {
        SortColumnValidator validator = SortColumnValidator.getInstance(SimplePageRequest.class);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("nonexistentColumn");

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validate_throwsBusinessRuleException_whenColumnSortIsNull() {
        SortColumnValidator validator = SortColumnValidator.getInstance(SimplePageRequest.class);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort(null);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessRuleException.class);
    }
}
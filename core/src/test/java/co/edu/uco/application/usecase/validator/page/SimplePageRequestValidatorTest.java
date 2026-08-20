package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimplePageRequestValidatorTest {

    @BeforeEach
    void setUp() {
        CatalogPort catalogPort = mock(CatalogPort.class);
        when(catalogPort.getMessage(anyString())).thenReturn("mensaje");
        when(catalogPort.getTitle(anyString())).thenReturn("titulo");
        CatalogPortStaticRef.set(catalogPort);
    }

    @Test
    void validate_delegatesToCompositeAndSortColumnValidator() {
        CatalogPort catalogPort = mock(CatalogPort.class);
        Validator<SimplePageRequest> fakeValidator = data -> {};
        CompositeValidator<SimplePageRequest> compositeValidator = new CompositeValidator<>(List.of(fakeValidator), catalogPort);
        SimplePageRequestValidator validator = new SimplePageRequestValidator(compositeValidator);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("id");

        validator.validate(request, MessageData.class);

        assertThat(compositeValidator).isNotNull();
    }

    @Test
    void validate_throwsBusinessRuleException_forInvalidSortColumn() {
        CatalogPort catalogPort = mock(CatalogPort.class);
        Validator<SimplePageRequest> fakeValidator = data -> {};
        CompositeValidator<SimplePageRequest> compositeValidator = new CompositeValidator<>(List.of(fakeValidator), catalogPort);
        SimplePageRequestValidator validator = new SimplePageRequestValidator(compositeValidator);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("invalidColumn");

        assertThatThrownBy(() -> validator.validate(request, MessageData.class))
                .isInstanceOf(co.edu.uco.crosscutting.exceptions.BusinessRuleException.class);
    }
}

package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimplePageRequestValidatorTest {

    @Mock
    private CompositeValidator<SimplePageRequest> compositeValidator;

    @Test
    void validate_delegatesToCompositeAndSortColumnValidator() {
        SimplePageRequestValidator validator = new SimplePageRequestValidator(compositeValidator);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("id");

        validator.validate(request, MessageData.class);

        verify(compositeValidator).validate(request);
    }

    @Test
    void validate_throwsBusinessRuleException_forInvalidSortColumn() {
        SimplePageRequestValidator validator = new SimplePageRequestValidator(compositeValidator);
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("invalidColumn");

        org.junit.jupiter.api.Assertions.assertThrows(
                co.edu.uco.crosscutting.exceptions.BusinessRuleException.class,
                () -> validator.validate(request, MessageData.class));
    }
}
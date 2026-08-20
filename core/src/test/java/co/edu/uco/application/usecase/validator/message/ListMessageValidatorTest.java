package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.page.SimplePageRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ListMessageValidatorTest {

    @Mock
    private SimplePageRequestValidator simplePageRequestValidator;

    @InjectMocks
    private ListMessageValidator validator;

    @Test
    void validate_delegatesToSimplePageRequestValidator() {
        SimplePageRequest request = new SimplePageRequest();
        request.setColumnSort("id");

        validator.validate(request);

        verify(simplePageRequestValidator).validate(request, MessageData.class);
    }
}
package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.page.SimplePageRequestCompositeValidator;
import org.springframework.stereotype.Component;

@Component
public final class ListMessageValidator {
    private final SimplePageRequestCompositeValidator pageRequestValidator;

    public ListMessageValidator(SimplePageRequestCompositeValidator pageRequestValidator) {
        this.pageRequestValidator = pageRequestValidator;
    }

    public void validate(SimplePageRequest data) {
        pageRequestValidator.validate(data, MessageData.class);
    }
}

package co.edu.uco.core.domain.validator.message;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.page.SimplePageRequestValidator;
import co.edu.uco.core.domain.validator.page.SortColumnValidator;
import org.springframework.stereotype.Component;

@Component
public final class ListMessageValidator {
    private final SimplePageRequestValidator simplePageRequestValidator;
    private final SortColumnValidator sortColumnValidator = SortColumnValidator.getInstance(MessageData.class);
    public ListMessageValidator(SimplePageRequestValidator simplePageRequestValidator) {
        this.simplePageRequestValidator = simplePageRequestValidator;
    }
    public void validate(SimplePageRequest data) {
        simplePageRequestValidator.validate(data, MessageData.class);
        sortColumnValidator.validate(data);
    }
}
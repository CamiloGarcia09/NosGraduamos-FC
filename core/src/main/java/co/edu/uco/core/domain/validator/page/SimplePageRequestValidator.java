package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.CompositeValidator;
import org.springframework.stereotype.Component;

@Component
public final class SimplePageRequestValidator{
    private final CompositeValidator<SimplePageRequest> compositeValidator;
    public SimplePageRequestValidator(CompositeValidator<SimplePageRequest> compositeValidator) {
        this.compositeValidator = compositeValidator;
    }
    public void validate(SimplePageRequest data, Class<?> modelClass) {
        compositeValidator.validate(data);
        SortColumnValidator.getInstance(modelClass).validate(data);
    }
}
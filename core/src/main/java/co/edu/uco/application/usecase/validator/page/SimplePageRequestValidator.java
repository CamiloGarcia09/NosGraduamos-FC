package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.CompositeValidator;
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
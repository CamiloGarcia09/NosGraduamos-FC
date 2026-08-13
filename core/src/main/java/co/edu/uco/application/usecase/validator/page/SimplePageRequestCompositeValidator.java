package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class SimplePageRequestCompositeValidator extends CompositeValidator<SimplePageRequest> {
    @Autowired
    public SimplePageRequestCompositeValidator(List<Validator<SimplePageRequest>> validators, CatalogPort catalogPort) {
        super(validators, catalogPort);
    }
}
package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.CompositeValidator;
import co.edu.uco.core.domain.validator.Validator;
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
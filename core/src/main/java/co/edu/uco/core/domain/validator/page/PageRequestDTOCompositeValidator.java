package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.validator.CompositeValidator;
import co.edu.uco.core.domain.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class PageRequestDTOCompositeValidator extends CompositeValidator<PageRequestDTO> {
    @Autowired
    public PageRequestDTOCompositeValidator(List<Validator<PageRequestDTO>> validators, InMemoryCatalog inMemoryCatalog) {
        super(validators, inMemoryCatalog);
    }
}
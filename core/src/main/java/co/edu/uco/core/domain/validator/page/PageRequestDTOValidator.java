package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.validator.CompositeValidator;
import org.springframework.stereotype.Component;

@Component
public final class PageRequestDTOValidator {
    private final CompositeValidator<PageRequestDTO> compositeValidator;
    public PageRequestDTOValidator(CompositeValidator<PageRequestDTO> compositeValidator) {
        this.compositeValidator = compositeValidator;
    }
    public void validate(PageRequestDTO data) {
        compositeValidator.validate(data);
    }
}
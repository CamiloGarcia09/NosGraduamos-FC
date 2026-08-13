package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public final class ExpirationDateValidator implements Validator<LocalDateTime> {
    private final CatalogPort catalogPort;
    public ExpirationDateValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_037"));
        }
    }
}
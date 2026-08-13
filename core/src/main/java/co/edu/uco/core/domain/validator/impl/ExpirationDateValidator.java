package co.edu.uco.core.domain.validator.impl;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
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
package co.edu.uco.core.domain.validator.impl;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilUUID.*;

@Component
public final class UUIDValidator implements Validator<String> {
    private final CatalogPort catalogPort;
    public UUIDValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(String uuid) {
        try {
            var value = getUUIDFromString(uuid);
            if (isEqual(value, DEFAULT_UUID)) {
                throw BusinessRuleException.buildTechnicalException(catalogPort.getMessage("FUN_038"));
            }
        } catch (CrossWordsException exception) {
            throw BusinessRuleException.buildUserException(exception.getTechnicalMessage());
        }
    }
}
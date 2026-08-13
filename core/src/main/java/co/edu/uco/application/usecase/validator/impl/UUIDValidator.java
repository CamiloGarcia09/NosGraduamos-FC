package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilUUID.*;

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
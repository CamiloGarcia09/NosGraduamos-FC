package co.edu.uco.core.domain.validator.impl;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilUUID.*;

@Component
public final class UUIDValidator implements Validator<String> {
    @Override
    public void validate(String uuid) {
        try {
            var value = getUUIDFromString(uuid);
            if (isEqual(value, DEFAULT_UUID)) {
                throw BusinessRuleException.buildTechnicalException(DetailMessageEnum.FUN_038.getContent());
            }
        } catch (CrossWordsException exception) {
            throw BusinessRuleException.buildUserException(exception.getTechnicalMessage());
        }
    }
}
package co.edu.uco.core.domain.validator.message;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;

@Component
public final class FindMessageCodeValidator implements Validator<String> {
    private final InMemoryCatalog inMemoryCatalog;
    public FindMessageCodeValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (isEmptyOrNull(data)) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_040.getKey()));
        }
    }
}
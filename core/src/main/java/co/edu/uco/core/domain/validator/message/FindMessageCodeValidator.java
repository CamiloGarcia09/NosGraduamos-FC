package co.edu.uco.core.domain.validator.message;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;

@Component
public final class FindMessageCodeValidator implements Validator<String> {
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (isEmptyOrNull(data)) {
            throw BusinessRuleException.buildUserException(DetailMessageEnum.FUN_040.getContent());
        }
    }
}
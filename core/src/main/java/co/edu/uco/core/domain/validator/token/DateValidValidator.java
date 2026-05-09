package co.edu.uco.core.domain.validator.token;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.DATE_PATTERN;
import static co.edu.uco.utils.helper.UtilDate.parseDate;

@Component
public final class DateValidValidator implements Validator<String> {
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (containsInvalidCharacters(data)) {
            throw BusinessRuleException.buildUserException(DetailMessageEnum.FUN_039.getContent());
        }
        try {
            parseDate(data);
        } catch (CrossWordsException exception) {
            throw BusinessRuleException.buildUserException(exception.getTechnicalMessage());
        }
    }
    private static boolean containsInvalidCharacters(String date) {
        return !date.matches(DATE_PATTERN);
    }
}
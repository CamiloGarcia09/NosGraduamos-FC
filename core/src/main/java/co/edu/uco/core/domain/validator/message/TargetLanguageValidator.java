package co.edu.uco.core.domain.validator.message;

import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;
import static co.edu.uco.utils.helper.UtilText.trim;

@Component
public final class TargetLanguageValidator implements Validator<String> {
    private static final String LANGUAGE_PATTERN = "^[a-zA-Z][a-zA-Z\\s_-]{1,49}$";

    @Override
    public void validate(String data) throws BusinessRuleException {
        var language = trim(data);
        if (isEmptyOrNull(language)) {
            throw BusinessRuleException.buildUserException("The target language is required.");
        }
        if (!language.matches(LANGUAGE_PATTERN)) {
            throw BusinessRuleException.buildUserException("The target language is invalid.");
        }
    }
}

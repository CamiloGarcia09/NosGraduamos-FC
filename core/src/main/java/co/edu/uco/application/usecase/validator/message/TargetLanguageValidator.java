package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Component
public final class TargetLanguageValidator implements Validator<String> {
    private static final String LANGUAGE_PATTERN = "^[a-zA-Z][a-zA-Z\\s_-]{1,49}$";
    private final CatalogPort catalogPort;

    public TargetLanguageValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }


    @Override
    public void validate(String data) throws BusinessRuleException {
        var language = trim(data);
        if (isEmptyOrNull(language)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_044.getCode()));
        }
        if (!language.matches(LANGUAGE_PATTERN)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_045.getCode()));
        }
    }
}

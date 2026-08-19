package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.application.CrosswordsConstant.DATE_PATTERN;
import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;

@Component
public final class DateValidValidator implements Validator<String> {
    private final CatalogPort catalogPort;
    public DateValidValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (containsInvalidCharacters(data)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_039.getCode()));
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
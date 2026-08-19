package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

@Component
public final class FindMessageCodeValidator implements Validator<String> {
    private final CatalogPort catalogPort;
    public FindMessageCodeValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (isEmptyOrNull(data)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_040.getCode()));
        }
    }
}
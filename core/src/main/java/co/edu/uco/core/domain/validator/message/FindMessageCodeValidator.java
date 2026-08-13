package co.edu.uco.core.domain.validator.message;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;

@Component
public final class FindMessageCodeValidator implements Validator<String> {
    private final CatalogPort catalogPort;
    public FindMessageCodeValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(String data) throws BusinessRuleException {
        if (isEmptyOrNull(data)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_040"));
        }
    }
}
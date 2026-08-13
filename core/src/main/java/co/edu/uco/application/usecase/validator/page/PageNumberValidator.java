package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.application.CrosswordsConstant.REQUEST_PAGE_DEFAULT;
import static co.edu.uco.crosscutting.helpers.UtilNumeric.isLessThan;

@Component
public final class PageNumberValidator implements Validator<SimplePageRequest> {
    private final CatalogPort catalogPort;
    public PageNumberValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (isLessThan(data.getPage(), REQUEST_PAGE_DEFAULT)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_032"));
        }
    }
}
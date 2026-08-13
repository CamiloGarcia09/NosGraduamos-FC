package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.application.CrosswordsConstant.REQUEST_PAGE_SORT_ASC;
import static co.edu.uco.application.CrosswordsConstant.REQUEST_PAGE_SORT_DESC;

@Component
public final class SortDirectionValidator implements Validator<SimplePageRequest> {
    private final CatalogPort catalogPort;
    public SortDirectionValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (!data.getSort().equalsIgnoreCase(REQUEST_PAGE_SORT_ASC) && !data.getSort().equalsIgnoreCase(REQUEST_PAGE_SORT_DESC)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_031"));
        }
    }
}
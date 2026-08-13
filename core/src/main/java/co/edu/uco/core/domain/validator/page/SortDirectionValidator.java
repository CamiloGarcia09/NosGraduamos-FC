package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_SORT_ASC;
import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_SORT_DESC;

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
package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.helper.UtilNumeric;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_DEFAULT;

@Component
public final class PageSizeValidator implements Validator<SimplePageRequest> {
    private static final int MAX_PAGE_SIZE = 100;
    private final CatalogPort catalogPort;
    public PageSizeValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (!UtilNumeric.isBetweenIncludingRanges(data.getSize(), REQUEST_PAGE_DEFAULT, MAX_PAGE_SIZE)) {
            throw BusinessRuleException.buildUserException(
                String.format(catalogPort.getMessage("FUN_029"), MAX_PAGE_SIZE));
        }
    }
}
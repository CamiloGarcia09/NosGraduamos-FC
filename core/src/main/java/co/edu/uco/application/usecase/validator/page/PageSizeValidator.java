package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.helpers.UtilNumeric;
import org.springframework.stereotype.Component;

import static co.edu.uco.application.CrosswordsConstant.REQUEST_PAGE_DEFAULT;

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
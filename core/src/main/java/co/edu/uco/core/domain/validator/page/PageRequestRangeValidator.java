package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.helper.UtilNumeric;
import org.springframework.stereotype.Component;

@Component
public final class PageRequestRangeValidator {
    private final CatalogPort catalogPort;
    public PageRequestRangeValidator(CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }
    public void validate(int page, int totalPages) {
        if (UtilNumeric.isGreaterThan(page, totalPages)) {
            throw BusinessRuleException.buildUserException(
                String.format(catalogPort.getMessage("FUN_028"), totalPages));
        }
    }
}
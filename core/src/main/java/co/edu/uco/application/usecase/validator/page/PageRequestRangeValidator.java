package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.helpers.UtilNumeric;
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
                String.format(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_028.getCode()), totalPages));
        }
    }
}
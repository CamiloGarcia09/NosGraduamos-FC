package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.helper.UtilNumeric;
import org.springframework.stereotype.Component;

@Component
public final class PageRequestRangeValidator {
    private final InMemoryCatalog inMemoryCatalog;
    public PageRequestRangeValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    public void validate(int page, int totalPages) {
        if (UtilNumeric.isGreaterThan(page, totalPages)) {
            throw BusinessRuleException.buildUserException(
                String.format(inMemoryCatalog.getContent(MessageKeyEnum.FUN_028.getKey()), totalPages));
        }
    }
}
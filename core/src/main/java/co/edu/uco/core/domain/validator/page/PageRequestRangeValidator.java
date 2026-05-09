package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.helper.UtilNumeric;
import org.springframework.stereotype.Component;

@Component
public final class PageRequestRangeValidator  {
    public void validate(int page, int totalPages) {
        if (UtilNumeric.isGreaterThan(page, totalPages)) {
            throw BusinessRuleException.buildUserException(String.format(DetailMessageEnum.FUN_028.getContent(), totalPages));
        }
    }
}
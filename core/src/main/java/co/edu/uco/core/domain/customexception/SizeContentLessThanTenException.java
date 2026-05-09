package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class SizeContentLessThanTenException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2529239574681102198L;
    private SizeContentLessThanTenException() {
        super(DetailMessageEnum.FUN_018.getTitle(), DetailMessageEnum.FUN_018.getContent());
    }
    public static void report() {
        throw new SizeContentLessThanTenException();
    }
}
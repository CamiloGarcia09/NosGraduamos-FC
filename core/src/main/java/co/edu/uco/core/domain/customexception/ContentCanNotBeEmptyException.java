package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class ContentCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2821910820329341124L;
    private ContentCanNotBeEmptyException() {
        super(DetailMessageEnum.FUN_017.getTitle(), DetailMessageEnum.FUN_017.getContent());
    }
    public static void report() {
        throw new ContentCanNotBeEmptyException();
    }
}
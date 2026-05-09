package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class TitleCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = 41765698692188909L;
    private TitleCanNotBeEmptyException() {
        super(DetailMessageEnum.FUN_022.getTitle(), DetailMessageEnum.FUN_022.getContent());
    }
    public static void report() {
            throw new TitleCanNotBeEmptyException();
        }
}
package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class SizeContentMoreThanOneHundred extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -4177616618105416722L;
    private SizeContentMoreThanOneHundred() {
        super(DetailMessageEnum.FUN_019.getTitle(), DetailMessageEnum.FUN_019.getContent());
    }
    public static void report() {
        throw new SizeContentMoreThanOneHundred();
    }
}
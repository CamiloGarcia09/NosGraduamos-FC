package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.InMemoryCatalogStaticRef;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class SizeContentMoreThanOneHundred extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -4177616618105416722L;
    private SizeContentMoreThanOneHundred() {
        super(
            InMemoryCatalogStaticRef.getTitle(MessageKeyEnum.FUN_019.getKey()),
            InMemoryCatalogStaticRef.getContent(MessageKeyEnum.FUN_019.getKey())
        );
    }
    public static void report() {
        throw new SizeContentMoreThanOneHundred();
    }
}
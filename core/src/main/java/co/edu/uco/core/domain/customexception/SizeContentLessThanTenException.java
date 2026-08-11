package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.InMemoryCatalogStaticRef;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class SizeContentLessThanTenException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2529239574681102198L;
    private SizeContentLessThanTenException() {
        super(
            InMemoryCatalogStaticRef.getTitle(MessageKeyEnum.FUN_018.getKey()),
            InMemoryCatalogStaticRef.getContent(MessageKeyEnum.FUN_018.getKey())
        );
    }
    public static void report() {
        throw new SizeContentLessThanTenException();
    }
}
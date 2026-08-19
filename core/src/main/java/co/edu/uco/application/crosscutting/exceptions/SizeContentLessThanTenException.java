package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class SizeContentLessThanTenException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2529239574681102198L;
    private SizeContentLessThanTenException() {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.FUN_018.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.FUN_018.getCode())
        );
    }
    public static void report() {
        throw new SizeContentLessThanTenException();
    }
}
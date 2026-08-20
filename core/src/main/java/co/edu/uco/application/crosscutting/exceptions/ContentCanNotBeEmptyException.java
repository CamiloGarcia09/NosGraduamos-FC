package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class ContentCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2821910820329341124L;
    private ContentCanNotBeEmptyException() {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.FUN_017.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.FUN_017.getCode())
        );
    }
    public static void report() {
        throw new ContentCanNotBeEmptyException();
    }
}
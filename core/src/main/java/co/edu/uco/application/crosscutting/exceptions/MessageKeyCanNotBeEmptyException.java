package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class MessageKeyCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageKeyCanNotBeEmptyException() {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.TCH_008.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_008.getCode())
        );
    }
    public static void report() {
        throw new MessageKeyCanNotBeEmptyException();
    }
}
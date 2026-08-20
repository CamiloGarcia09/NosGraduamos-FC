package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class MessageNotFoundException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageNotFoundException(String key) {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.TCH_009.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_009.getCode()).formatted(key)
        );
    }
    public static void report(String key) {
        throw new MessageNotFoundException(key);
    }
}
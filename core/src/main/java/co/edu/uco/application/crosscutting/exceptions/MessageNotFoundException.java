package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;

import java.io.Serial;

public final class MessageNotFoundException extends CrossWordsException {
    @Serial
    private static final long serialVersionUID = 1L;
    private MessageNotFoundException(String key) {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.TCH_009.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_009.getCode()).formatted(key),
            null,
            ExceptionType.BUSINESS_RULE,
            ExceptionLocation.APPLICATION
        );
    }
    public static void report(String key) {
        throw new MessageNotFoundException(key);
    }
}
package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class TitleCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = 41765698692188909L;
    private TitleCanNotBeEmptyException() {
        super(
            CatalogPortStaticRef.getTitle(MessageCatalogCodeEnum.FUN_022.getCode()),
            CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.FUN_022.getCode())
        );
    }
    public static void report() {
            throw new TitleCanNotBeEmptyException();
        }
}
package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.CatalogPortStaticRef;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class TitleCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = 41765698692188909L;
    private TitleCanNotBeEmptyException() {
        super(
            CatalogPortStaticRef.getTitle("FUN_022"),
            CatalogPortStaticRef.getMessage("FUN_022")
        );
    }
    public static void report() {
            throw new TitleCanNotBeEmptyException();
        }
}
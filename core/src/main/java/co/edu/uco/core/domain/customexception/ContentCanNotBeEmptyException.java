package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.CatalogPortStaticRef;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class ContentCanNotBeEmptyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2821910820329341124L;
    private ContentCanNotBeEmptyException() {
        super(
            CatalogPortStaticRef.getTitle("FUN_017"),
            CatalogPortStaticRef.getMessage("FUN_017")
        );
    }
    public static void report() {
        throw new ContentCanNotBeEmptyException();
    }
}
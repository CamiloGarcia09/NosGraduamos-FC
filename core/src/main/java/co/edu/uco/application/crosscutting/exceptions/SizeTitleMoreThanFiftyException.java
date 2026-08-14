package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import java.io.Serial;

public final class SizeTitleMoreThanFiftyException extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -2432315861505641573L;
    private SizeTitleMoreThanFiftyException() {
        super(
            CatalogPortStaticRef.getTitle("FUN_021"),
            CatalogPortStaticRef.getMessage("FUN_021")
        );
    }
    public static void report() {
        throw new SizeTitleMoreThanFiftyException();
    }
}
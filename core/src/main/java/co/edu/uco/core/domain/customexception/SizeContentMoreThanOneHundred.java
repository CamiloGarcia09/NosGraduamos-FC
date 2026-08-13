package co.edu.uco.core.domain.customexception;

import co.edu.uco.core.application.catalog.CatalogPortStaticRef;
import co.edu.uco.utils.exception.BusinessRuleException;

import java.io.Serial;

public final class SizeContentMoreThanOneHundred extends BusinessRuleException {
    @Serial
    private static final long serialVersionUID = -4177616618105416722L;
    private SizeContentMoreThanOneHundred() {
        super(
            CatalogPortStaticRef.getTitle("FUN_019"),
            CatalogPortStaticRef.getMessage("FUN_019")
        );
    }
    public static void report() {
        throw new SizeContentMoreThanOneHundred();
    }
}
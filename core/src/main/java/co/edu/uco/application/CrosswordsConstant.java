package co.edu.uco.application;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;

public final class CrosswordsConstant {
    private CrosswordsConstant() {}
    public static final String SINGLETON_SCOPE = "singleton";
    public static final String REQUEST_COLUMN_SORT_DEFAULT = CatalogPortStaticRef.getMessage("FUN_060");
    public static final String TOKEN_SECRET_IDENTIFIER = CatalogPortStaticRef.getMessage("FUN_061");
    public static final String SECRET_PORT_SECRET_NAME = CatalogPortStaticRef.getMessage("FUN_062");
    public static final String SECRET_PORT_PRIVATE_KEY = CatalogPortStaticRef.getMessage("FUN_063");
    public static final String DATE_PATTERN = CatalogPortStaticRef.getMessage("FUN_064");
    public static final String PAGE_ATTRIBUTE = CatalogPortStaticRef.getMessage("FUN_065");
    public static final String SIZE_ATTRIBUTE = CatalogPortStaticRef.getMessage("FUN_066");
    public static final String COLUMN_SORT_ATTRIBUTE = CatalogPortStaticRef.getMessage("FUN_067");
    public static final String SORT_ATTRIBUTE = CatalogPortStaticRef.getMessage("FUN_068");
    public static final String STATE_ACTIVE = CatalogPortStaticRef.getMessage("FUN_069");
    public static final String STATE_INACTIVE = CatalogPortStaticRef.getMessage("FUN_070");
    public static final String REQUEST_PAGE_SORT_ASC = CatalogPortStaticRef.getMessage("FUN_071");
    public static final String REQUEST_PAGE_SORT_DESC = CatalogPortStaticRef.getMessage("FUN_072");
    public static final byte REQUEST_PAGE_DEFAULT = 1;
    public static final byte REQUEST_SIZE_DEFAULT = 50;
    public static final String TOKEN_STATE_ACTIVE_ID = CatalogPortStaticRef.getMessage("FUN_073");
    public static final String TOKEN_STATE_INACTIVE_ID = CatalogPortStaticRef.getMessage("FUN_144");
}
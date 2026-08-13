package co.edu.uco.application.common.catalog;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;

/**
 * Static holder so non-Spring code (domain exceptions, serializers) can resolve
 * catalog messages without requiring Spring injection.
 * Populated by RedisCatalogMessageAdapter on @PostConstruct.
 */
public final class CatalogPortStaticRef {

    private static volatile CatalogPort instance;

    private CatalogPortStaticRef() {}

    public static void set(CatalogPort catalog) {
        instance = catalog;
    }

    public static String getMessage(String key) {
        return instance != null ? instance.getMessage(key) : "";
    }

    public static String getMessage(String key, String defaultMessage) {
        return instance != null ? instance.getMessage(key, defaultMessage) : defaultMessage;
    }

    public static String getTitle(String key) {
        return instance != null ? instance.getTitle(key) : "";
    }

    public static MessageCatalog getMessageModel(String key) {
        return instance != null ? instance.getMessageModel(key) : null;
    }
}
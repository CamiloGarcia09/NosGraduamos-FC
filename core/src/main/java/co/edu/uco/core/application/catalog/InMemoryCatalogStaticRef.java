package co.edu.uco.core.application.catalog;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;

/**
 * Static holder so non-Spring code (domain exceptions, serializers) can resolve
 * catalog messages without requiring Spring injection.
 * Populated by InMemoryMessageCatalog on @PostConstruct.
 */
public final class InMemoryCatalogStaticRef {

    private static volatile InMemoryCatalog instance;

    private InMemoryCatalogStaticRef() {}

    public static void set(InMemoryCatalog catalog) {
        instance = catalog;
    }

    public static String getContent(String key) {
        return instance != null ? instance.getContent(key) : "";
    }

    public static String getTitle(String key) {
        if (instance == null) return "";
        var model = instance.getMessageById(
                co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum.of(key));
        return model != null ? model.title() : "";
    }
}

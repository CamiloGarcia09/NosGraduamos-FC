package co.edu.uco.core.domain.port.out.catalog;

import co.edu.uco.core.application.catalog.MessageCatalog;

public interface CatalogPort {

    MessageCatalog getMessageModel(String key);
    String getMessage(String key);
    String getMessage(String key, String defaultMessage);
    String getTitle(String key);
    void setMessage(String key, MessageCatalog message);
}
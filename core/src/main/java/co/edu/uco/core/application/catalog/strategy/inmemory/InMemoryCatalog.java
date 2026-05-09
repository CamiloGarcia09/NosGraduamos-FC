package co.edu.uco.core.application.catalog.strategy.inmemory;

import co.edu.uco.core.application.catalog.strategy.MessageCatalog;
import co.edu.uco.core.application.catalog.MessageModel;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;

public abstract class InMemoryCatalog extends MessageCatalog<MessageKeyEnum, MessageModel> {
    public abstract void loadCatalog();
    public abstract void reloadCatalog();
    public abstract void addMessage(MessageKeyEnum key, MessageModel messageModel);
}
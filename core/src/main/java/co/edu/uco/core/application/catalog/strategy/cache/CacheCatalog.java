package co.edu.uco.core.application.catalog.strategy.cache;

import co.edu.uco.core.application.catalog.strategy.MessageCatalog;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;

import java.util.Optional;

public abstract class CacheCatalog extends MessageCatalog<String, Optional<MessageData>> {
    public abstract void addMessage(MessageData messageModel);
    public abstract void addMessageWithEnvironment(MessageData messageModel, String environmentId);
    public abstract SimplePage<MessageData> getMessageWithEnvironment(String environment, SimplePageRequest request);
    public abstract Optional<MessageData> getMessageByCodeAndEnvironment(String code, String environmentId);
}
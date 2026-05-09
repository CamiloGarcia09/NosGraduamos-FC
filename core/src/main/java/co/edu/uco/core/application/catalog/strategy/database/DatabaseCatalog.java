package co.edu.uco.core.application.catalog.strategy.database;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.application.catalog.strategy.MessageCatalog;

import java.util.Optional;

public abstract class DatabaseCatalog extends MessageCatalog<String, Optional<MessageData>> {
    public abstract SimplePage<MessageData> getMessageWithEnvironment(String environment, SimplePageRequest request);
    public abstract Optional<MessageData> getMessageByCodeAndEnvironment(String code, String environmentId);
}
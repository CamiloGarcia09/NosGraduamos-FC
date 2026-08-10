package co.edu.uco.core.application.catalog.strategy;

import co.edu.uco.core.application.catalog.strategy.cache.CacheCatalog;
import co.edu.uco.core.application.catalog.strategy.database.DatabaseCatalog;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPortFactory;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.utils.exception.BusinessException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum.*;

import java.util.Optional;

@Component
public final class MessageCatalogStrategy {
    private final CacheCatalog cacheCatalog;
    private final DatabaseCatalog databaseCatalog;
    private final InMemoryCatalog inMemoryCatalog;
    private final LoggingPort log;

    public MessageCatalogStrategy(CacheCatalog cacheCatalog, DatabaseCatalog databaseCatalog,
            InMemoryCatalog inMemoryCatalog, LoggingPortFactory loggerFactory) {
        this.cacheCatalog = cacheCatalog;
        this.databaseCatalog = databaseCatalog;
        this.inMemoryCatalog = inMemoryCatalog;
        this.log = loggerFactory.getLogger(MessageCatalogStrategy.class);
    }
    public SimplePage<MessageData> getMessagesWithEnvironment(String environment, SimplePageRequest request) {
        var cachedMessages = cacheCatalog.getMessageWithEnvironment(environment, request);
        if (cachedMessages.getData().isEmpty()) {
            log.info(inMemoryCatalog.getContent(FUN_006.getKey()));
            var dbMessages = databaseCatalog.getMessageWithEnvironment(environment, request);
            if (!dbMessages.getData().isEmpty()) {
                log.info(inMemoryCatalog.getContent(FUN_007.getKey()));
                fillCacheWithEnvironmentMessages(dbMessages, environment);
                return dbMessages;
            }
            throw BusinessException.buildUserException(inMemoryCatalog.getContent(TCH_009.getKey()));
        }

        var dbMessages = databaseCatalog.getMessageWithEnvironment(environment, request);
        if (!dbMessages.getData().isEmpty()) {
            if (cachedMessages.getData().size() != dbMessages.getData().size()) {
                log.info(inMemoryCatalog.getContent(FUN_008.getKey()));
                fillCacheWithEnvironmentMessages(dbMessages, environment);
                return dbMessages;
            }
            log.info(inMemoryCatalog.getContent(FUN_009.getKey()));
            return cachedMessages;
        }
        throw BusinessException.buildUserException(inMemoryCatalog.getContent(TCH_009.getKey()));
    }
    public Optional<MessageData> getMessageByCodeAndEnvironment(String code, String environmentId) {
        var response = cacheCatalog.getMessageByCodeAndEnvironment(code, environmentId);
        if (response.isPresent()) {
            log.info(inMemoryCatalog.getContent(FUN_009.getKey()));
        }
        if (response.isEmpty()) {
            log.info(inMemoryCatalog.getContent(FUN_006.getKey()));
            response = databaseCatalog.getMessageByCodeAndEnvironment(code, environmentId);
            response.ifPresent(message -> cacheCatalog.addMessageWithEnvironment(message, environmentId));
        }
        return response;
    }
    private void fillCacheWithEnvironmentMessages(SimplePage<MessageData> dbMessages, String environment) {
        dbMessages.getData().forEach(message -> cacheCatalog.addMessageWithEnvironment(message, environment));
    }
}
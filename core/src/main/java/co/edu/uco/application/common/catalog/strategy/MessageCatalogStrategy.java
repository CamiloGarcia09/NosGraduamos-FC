package co.edu.uco.application.common.catalog.strategy;

import co.edu.uco.application.common.catalog.strategy.cache.CacheCatalog;
import co.edu.uco.application.common.catalog.strategy.database.DatabaseCatalog;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class MessageCatalogStrategy {
    private final CacheCatalog cacheCatalog;
    private final DatabaseCatalog databaseCatalog;
    private final CatalogPort catalogPort;
    private final LoggingPort log;

    public MessageCatalogStrategy(CacheCatalog cacheCatalog, DatabaseCatalog databaseCatalog,
            CatalogPort catalogPort, LoggingPortFactory loggerFactory) {
        this.cacheCatalog = cacheCatalog;
        this.databaseCatalog = databaseCatalog;
        this.catalogPort = catalogPort;
        this.log = loggerFactory.getLogger(MessageCatalogStrategy.class);
    }
    public SimplePage<MessageData> getMessagesWithEnvironment(String environment, SimplePageRequest request) {
        var cachedMessages = cacheCatalog.getMessageWithEnvironment(environment, request);
        if (cachedMessages.getData().isEmpty()) {
            log.info(catalogPort.getMessage("FUN_006"));
            var dbMessages = databaseCatalog.getMessageWithEnvironment(environment, request);
            if (!dbMessages.getData().isEmpty()) {
                log.info(catalogPort.getMessage("FUN_007"));
                fillCacheWithEnvironmentMessages(dbMessages, environment);
                return dbMessages;
            }
            throw BusinessException.buildUserException(catalogPort.getMessage("TCH_009"));
        }

        var dbMessages = databaseCatalog.getMessageWithEnvironment(environment, request);
        if (!dbMessages.getData().isEmpty()) {
            if (cachedMessages.getData().size() != dbMessages.getData().size()) {
                log.info(catalogPort.getMessage("FUN_008"));
                fillCacheWithEnvironmentMessages(dbMessages, environment);
                return dbMessages;
            }
            log.info(catalogPort.getMessage("FUN_009"));
            return cachedMessages;
        }
        throw BusinessException.buildUserException(catalogPort.getMessage("TCH_009"));
    }
    public Optional<MessageData> getMessageByCodeAndEnvironment(String code, String environmentId) {
        var response = cacheCatalog.getMessageByCodeAndEnvironment(code, environmentId);
        if (response.isPresent()) {
            log.info(catalogPort.getMessage("FUN_009"));
        }
        if (response.isEmpty()) {
            log.info(catalogPort.getMessage("FUN_006"));
            response = databaseCatalog.getMessageByCodeAndEnvironment(code, environmentId);
            response.ifPresent(message -> cacheCatalog.addMessageWithEnvironment(message, environmentId));
        }
        return response;
    }
    private void fillCacheWithEnvironmentMessages(SimplePage<MessageData> dbMessages, String environment) {
        dbMessages.getData().forEach(message -> cacheCatalog.addMessageWithEnvironment(message, environment));
    }

    /** Returns internal system message content by code; sourced from Redis via CatalogPort. */
    public String getSystemMessageContent(String code) {
        return catalogPort.getMessage(code);
    }
}
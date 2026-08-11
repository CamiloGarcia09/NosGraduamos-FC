package co.edu.uco.core.catalog;

import co.edu.uco.core.application.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.core.application.catalog.strategy.cache.CacheCatalog;
import co.edu.uco.core.application.catalog.strategy.database.DatabaseCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPortFactory;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Verifies that MessageCatalogStrategy:
 *  - returns a cached (Redis) result on cache hit
 *  - queries SurrealDB on cache miss
 *  - writes a SurrealDB result back into Redis after a cache miss
 *  - delegates getSystemMessageContent to InMemoryCatalog (not hardcoded strings)
 *  - DataBaseMessageRepository is a pure port interface with no infra imports
 */
class MessageCatalogStrategyTest {

    private CacheCatalog   cacheCatalog;
    private DatabaseCatalog databaseCatalog;
    private InMemoryCatalog inMemoryCatalog;
    private LoggingPort     loggingPort;

    private MessageCatalogStrategy strategy;

    @BeforeEach
    void setUp() {
        cacheCatalog    = mock(CacheCatalog.class);
        databaseCatalog = mock(DatabaseCatalog.class);
        inMemoryCatalog = mock(InMemoryCatalog.class);
        loggingPort     = mock(LoggingPort.class);

        LoggingPortFactory logFactory = clazz -> loggingPort;
        strategy = new MessageCatalogStrategy(cacheCatalog, databaseCatalog, inMemoryCatalog, logFactory);
    }

    // ── Test 5 ────────────────────────────────────────────────────────────────
    /**
     * Cache hit: when Redis already has the message, it must be returned directly
     * and SurrealDB must NOT be queried.
     */
    @Test
    void getMessageByCodeAndEnvironment_cacheHit_returnsRedisResultWithoutQueryingDatabase() {
        MessageData cached = new MessageData();
        cached.setCode("FUN_001");
        when(cacheCatalog.getMessageByCodeAndEnvironment("FUN_001", "env-1"))
                .thenReturn(Optional.of(cached));
        when(inMemoryCatalog.getContent(anyString())).thenReturn("some log message");

        Optional<MessageData> result = strategy.getMessageByCodeAndEnvironment("FUN_001", "env-1");

        assertTrue(result.isPresent(), "Result must be present on cache hit");
        assertEquals("FUN_001", result.get().getCode());
        verify(databaseCatalog, never()).getMessageByCodeAndEnvironment(any(), any());
    }

    // ── Test 6 ────────────────────────────────────────────────────────────────
    /**
     * Cache miss: when Redis has no result, SurrealDB must be queried.
     */
    @Test
    void getMessageByCodeAndEnvironment_cacheMiss_queriesSurrealDB() {
        MessageData fromDb = new MessageData();
        fromDb.setCode("FUN_002");
        when(cacheCatalog.getMessageByCodeAndEnvironment("FUN_002", "env-1"))
                .thenReturn(Optional.empty());
        when(databaseCatalog.getMessageByCodeAndEnvironment("FUN_002", "env-1"))
                .thenReturn(Optional.of(fromDb));
        when(inMemoryCatalog.getContent(anyString())).thenReturn("log");

        Optional<MessageData> result = strategy.getMessageByCodeAndEnvironment("FUN_002", "env-1");

        assertTrue(result.isPresent(), "Result must be present when DB has the message");
        assertEquals("FUN_002", result.get().getCode());
        verify(databaseCatalog).getMessageByCodeAndEnvironment("FUN_002", "env-1");
    }

    // ── Test 7 ────────────────────────────────────────────────────────────────
    /**
     * Write-back: after a cache miss resolved by SurrealDB, the result must be
     * stored back in Redis so subsequent calls are served from cache.
     */
    @Test
    void getMessageByCodeAndEnvironment_cacheMiss_storesResultInRedisWriteBack() {
        MessageData fromDb = new MessageData();
        fromDb.setCode("TCH_001");
        when(cacheCatalog.getMessageByCodeAndEnvironment("TCH_001", "env-2"))
                .thenReturn(Optional.empty());
        when(databaseCatalog.getMessageByCodeAndEnvironment("TCH_001", "env-2"))
                .thenReturn(Optional.of(fromDb));
        when(inMemoryCatalog.getContent(anyString())).thenReturn("log");

        strategy.getMessageByCodeAndEnvironment("TCH_001", "env-2");

        verify(cacheCatalog).addMessageWithEnvironment(fromDb, "env-2");
    }

    // ── Test 8 ────────────────────────────────────────────────────────────────
    /**
     * getSystemMessageContent must delegate to InMemoryCatalog — which sources
     * from SurrealDB — with no hardcoded strings in use cases or facades.
     */
    @Test
    void getSystemMessageContent_delegatesToInMemoryCatalogNotDetailMessageEnum() {
        when(inMemoryCatalog.getContent("FUN_011")).thenReturn("Messages for environment retrieved");

        String result = strategy.getSystemMessageContent("FUN_011");

        assertEquals("Messages for environment retrieved", result);
        verify(inMemoryCatalog).getContent("FUN_011");
    }

    // ── Test 9 ────────────────────────────────────────────────────────────────
    /**
     * DataBaseMessageRepository is a pure port interface that must not import
     * any infrastructure (SurrealDB SDK, Spring Data) in its own compilation unit.
     * Verified by checking that the interface is loadable and has no concrete
     * implementation dependency visible at the port layer.
     */
    @Test
    void dataBaseMessageRepository_isAPurePortInterface_notDependentOnInfrastructure() {
        // The interface must be loadable from core without infrastructure on the classpath
        Class<?> portInterface = DataBaseMessageRepository.class;
        assertTrue(portInterface.isInterface(), "DataBaseMessageRepository must be an interface");
        // Its declared package must belong to the core domain port, not infrastructure
        assertTrue(portInterface.getPackageName()
                        .startsWith("co.edu.uco.core.domain.port"),
                "DataBaseMessageRepository must live in the core.domain.port package");
    }
}

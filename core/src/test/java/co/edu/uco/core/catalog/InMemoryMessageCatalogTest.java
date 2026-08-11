package co.edu.uco.core.catalog;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryMessageCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.data.MessageCategoryData;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.data.MessageTypeData;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Verifies that InMemoryMessageCatalog:
 *  - returns empty string when no DB is wired (SurrealDB is the only source)
 *  - loads content from SurrealDB when DB is available (SurrealDB as source of truth)
 *  - gracefully handles DB failures per key without crashing
 *  - reflects runtime content changes without recompilation
 */
class InMemoryMessageCatalogTest {

    private InMemoryMessageCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new InMemoryMessageCatalog();
    }

    // ── Test 1 ────────────────────────────────────────────────────────────────
    /**
     * Without any DB wired, the catalog must be empty — no hardcoded fallback.
     * getContent() must return empty string (not throw, not return stale data).
     */
    @Test
    void loadCatalog_withoutDatabase_catalogIsEmpty() {
        catalog.loadCatalog();

        String content = catalog.getContent(MessageKeyEnum.FUN_001.getKey());
        assertNotNull(content, "getContent must never return null");
        assertTrue(content.isEmpty(), "Without DB, catalog must return empty string — SurrealDB is the only source");
    }

    // ── Test 2 ────────────────────────────────────────────────────────────────
    /**
     * When SurrealDB returns a message, its content must be stored and returned —
     * proving SurrealDB is the single source of truth.
     */
    @Test
    void loadCatalog_withDatabase_storesDatabaseContent() throws Exception {
        DataBaseMessageRepository mockRepo = mock(DataBaseMessageRepository.class);
        injectRepo(mockRepo);

        String dbContent = "DB-DRIVEN CONTENT FOR FUN_001";
        MessageData dbMessage = buildMessageData("FUN_001", dbContent, "DB Title", "Functional", "Information");
        when(mockRepo.findByCode("FUN_001")).thenReturn(Optional.of(dbMessage));
        when(mockRepo.findByCode(argThat(c -> !"FUN_001".equals(c)))).thenReturn(Optional.empty());

        catalog.loadCatalog();

        assertEquals(dbContent, catalog.getContent(MessageKeyEnum.FUN_001.getKey()),
                "SurrealDB content must be the single source of truth");
    }

    // ── Test 3 ────────────────────────────────────────────────────────────────
    /**
     * Changing a message in SurrealDB (via reloadCatalog) is reflected immediately
     * without recompiling — demonstrating runtime content management.
     */
    @Test
    void reloadCatalog_reflectsUpdatedDatabaseContentWithoutRecompile() throws Exception {
        DataBaseMessageRepository mockRepo = mock(DataBaseMessageRepository.class);
        injectRepo(mockRepo);

        String originalContent = "ORIGINAL CONTENT";
        String updatedContent  = "UPDATED CONTENT FROM SURREALDB";

        MessageData original = buildMessageData("FUN_002", originalContent, "Title", "Functional", "Warning");
        MessageData updated  = buildMessageData("FUN_002", updatedContent,  "Title", "Functional", "Warning");

        when(mockRepo.findByCode("FUN_002")).thenReturn(Optional.of(original));
        when(mockRepo.findByCode(argThat(c -> !"FUN_002".equals(c)))).thenReturn(Optional.empty());
        catalog.loadCatalog();
        assertEquals(originalContent, catalog.getContent(MessageKeyEnum.FUN_002.getKey()));

        when(mockRepo.findByCode("FUN_002")).thenReturn(Optional.of(updated));
        catalog.reloadCatalog();
        assertEquals(updatedContent, catalog.getContent(MessageKeyEnum.FUN_002.getKey()),
                "reloadCatalog must reflect updated DB content without recompile");
    }

    // ── Test 4 ────────────────────────────────────────────────────────────────
    /**
     * When the DB throws for a specific key, the catalog must not crash and
     * must return empty string for that key (no hardcoded fallback).
     */
    @Test
    void loadCatalog_whenDatabaseThrowsForOneKey_catalogIsEmptyForThatKey() throws Exception {
        DataBaseMessageRepository mockRepo = mock(DataBaseMessageRepository.class);
        injectRepo(mockRepo);

        when(mockRepo.findByCode("FUN_003")).thenThrow(new RuntimeException("DB transient error"));
        when(mockRepo.findByCode(argThat(c -> !"FUN_003".equals(c)))).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> catalog.loadCatalog(),
                "DB failure for one key must not crash the catalog load");

        String content = catalog.getContent(MessageKeyEnum.FUN_003.getKey());
        assertNotNull(content, "getContent must never return null");
        assertTrue(content.isEmpty(),
                "Key with DB failure must resolve to empty string — no hardcoded fallback");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void injectRepo(DataBaseMessageRepository repo) throws Exception {
        Field field = InMemoryMessageCatalog.class.getDeclaredField("dataBaseMessageRepository");
        field.setAccessible(true);
        field.set(catalog, repo);
    }

    private MessageData buildMessageData(String code, String content, String title,
                                         String typeName, String categoryName) {
        MessageData data = new MessageData();
        data.setCode(code);
        data.setContent(content);
        data.setTitle(title);
        data.setType(MessageTypeData.build(typeName));
        data.setCategory(MessageCategoryData.build(categoryName));
        return data;
    }
}

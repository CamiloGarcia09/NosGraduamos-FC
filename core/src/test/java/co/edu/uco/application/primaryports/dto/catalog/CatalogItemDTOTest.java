package co.edu.uco.application.primaryports.dto.catalog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogItemDTOTest {

    @Test
    void create_trimsIdAndName() {
        CatalogItemDTO dto = CatalogItemDTO.create("  app-1  ", "  Message App  ");

        assertAll(
                () -> assertEquals("app-1", dto.id()),
                () -> assertEquals("Message App", dto.name()));
    }

    @Test
    void constructor_trimsIdAndName() {
        CatalogItemDTO dto = new CatalogItemDTO("  app-1  ", "  env-1  ");

        assertAll(
                () -> assertEquals("app-1", dto.id()),
                () -> assertEquals("env-1", dto.name()));
    }
}
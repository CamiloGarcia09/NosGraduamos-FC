package co.edu.uco.application.primaryports.dto.catalog;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;

public record CatalogItemDTO(String id, String name) {

    public CatalogItemDTO(String id, String name) {
        this.id = trim(id);
        this.name = trim(name);
    }

    public static CatalogItemDTO create(String id, String name) {
        return new CatalogItemDTO(id, name);
    }
}

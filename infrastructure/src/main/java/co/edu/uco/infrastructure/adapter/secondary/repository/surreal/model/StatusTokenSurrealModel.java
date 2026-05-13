package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model;

import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class StatusTokenSurrealModel {

    private UUID id;
    private String name;

    public StatusTokenSurrealModel(UUID id, String name) {
        setId(id);
        setName(name);
    }

    public StatusTokenSurrealModel() {
        setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        setName(EMPTY);
    }

    public void setId(UUID id) { this.id = getDefaultUUID(id); }

    public void setName(String name) { this.name = trim(name); }

    public static StatusTokenSurrealModel build() { return new StatusTokenSurrealModel(); }
}

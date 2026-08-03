package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.utils.helper.UtilDate.TIME;
import static co.edu.uco.utils.helper.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;
import static co.edu.uco.utils.helper.UtilUUID.getNewUUID;

@Getter
public final class EnvironmentSurrealModel {

    private UUID id;
    private String name;
    private String applicationId;
    private String typeId;
    private String stateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EnvironmentSurrealModel(UUID id, String name, String applicationId, String typeId,
                                   String stateId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        setId(id);
        setName(name);
        setApplicationId(applicationId);
        setTypeId(typeId);
        setStateId(stateId);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    public EnvironmentSurrealModel() {
        setId(getNewUUID());
        setName(EMPTY);
        setApplicationId(EMPTY);
        setTypeId(EMPTY);
        setStateId(EMPTY);
        setCreatedAt(TIME);
        setUpdatedAt(TIME);
    }

    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setName(String name) {
        this.name = trim(name);
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = trim(applicationId);
    }

    public void setTypeId(String typeId) {
        this.typeId = trim(typeId);
    }

    public void setStateId(String stateId) {
        this.stateId = trim(stateId);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = getDefaultTimeIfNull(createdAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = getDefaultTimeIfNull(updatedAt);
    }

    public static EnvironmentSurrealModel build() {
        return new EnvironmentSurrealModel();
    }
}

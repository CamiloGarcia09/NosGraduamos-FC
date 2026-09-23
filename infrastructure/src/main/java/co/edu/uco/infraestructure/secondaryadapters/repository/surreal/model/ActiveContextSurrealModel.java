package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.crosscutting.helpers.UtilDate.nowUtc;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class ActiveContextSurrealModel {

    private UUID id;
    private UUID externalIdentityId;
    private UUID organizationId;
    private UUID applicationId;
    private UUID environmentId;
    private LocalDateTime updatedAt;

    public ActiveContextSurrealModel(final UUID id, final UUID externalIdentityId, final UUID organizationId,
                                     final UUID applicationId, final UUID environmentId,
                                     final LocalDateTime updatedAt) {
        setId(id);
        setExternalIdentityId(externalIdentityId);
        setOrganizationId(organizationId);
        setApplicationId(applicationId);
        setEnvironmentId(environmentId);
        setUpdatedAt(updatedAt);
    }

    public ActiveContextSurrealModel() {
        setId(null);
        setExternalIdentityId(null);
        setOrganizationId(null);
        setApplicationId(null);
        setEnvironmentId(null);
        setUpdatedAt(nowUtc());
    }

    public void setId(final UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setExternalIdentityId(final UUID externalIdentityId) {
        this.externalIdentityId = getDefaultUUID(externalIdentityId);
    }

    public void setOrganizationId(final UUID organizationId) {
        this.organizationId = getDefaultUUID(organizationId);
    }

    public void setApplicationId(final UUID applicationId) {
        this.applicationId = getDefaultUUID(applicationId);
    }

    public void setEnvironmentId(final UUID environmentId) {
        this.environmentId = getDefaultUUID(environmentId);
    }

    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = getDefaultTimeIfNull(updatedAt);
    }
}

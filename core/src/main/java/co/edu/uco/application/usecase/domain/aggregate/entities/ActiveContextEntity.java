package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class ActiveContextEntity extends Entity<UUID> {

    private UUID id;
    private UUID externalIdentityId;
    private UUID organizationId;
    private UUID applicationId;
    private UUID environmentId;
    private LocalDateTime updatedAt;

    @Override
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

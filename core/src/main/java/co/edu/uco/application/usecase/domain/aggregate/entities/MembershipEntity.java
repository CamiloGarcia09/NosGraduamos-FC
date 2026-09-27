package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class MembershipEntity extends Entity<UUID> {

    private UUID id;
    private UUID externalIdentityId;
    private UUID organizationId;

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
}

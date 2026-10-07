package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class RoleAssignmentEntity extends Entity<UUID> {

    private UUID id;
    private UUID membershipId;
    private UUID roleId;
    private UUID organizationId;

    @Override
    public void setId(final UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setMembershipId(final UUID membershipId) {
        this.membershipId = getDefaultUUID(membershipId);
    }

    public void setRoleId(final UUID roleId) {
        this.roleId = getDefaultUUID(roleId);
    }

    public void setOrganizationId(final UUID organizationId) {
        this.organizationId = getDefaultUUID(organizationId);
    }
}

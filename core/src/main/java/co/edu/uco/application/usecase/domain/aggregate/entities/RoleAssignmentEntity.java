package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class RoleAssignmentEntity extends Entity<UUID> {

    private UUID id;
    private UUID membershipId;
    private UUID roleId;
    private AuthorizationScopeType scopeType;
    private UUID organizationId;
    private UUID applicationId;
    private UUID environmentId;

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

    public void setScopeType(final AuthorizationScopeType scopeType) {
        this.scopeType = scopeType;
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
}

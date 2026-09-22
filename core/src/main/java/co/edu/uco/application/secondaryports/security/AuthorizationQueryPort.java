package co.edu.uco.application.secondaryports.security;

import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;

import java.util.List;
import java.util.UUID;

public interface AuthorizationQueryPort {

    boolean hasPermission(ExternalIdentity identity, PermissionCode permission,
                          AuthorizationScopeType scopeType, UUID scopeId);

    List<UUID> findAuthorizedApplicationIds(ExternalIdentity identity, PermissionCode permission);

    List<UUID> findAuthorizedEnvironmentIds(ExternalIdentity identity, PermissionCode permission,
                                             UUID applicationId);
}

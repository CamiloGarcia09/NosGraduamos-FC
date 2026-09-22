package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;

import java.util.UUID;

public interface AuthorizationRule {

    void validate(ExternalIdentity identity, PermissionCode permission,
                  AuthorizationScopeType scopeType, UUID scopeId);
}

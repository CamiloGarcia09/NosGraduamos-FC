package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;

import java.util.UUID;

public final class AuthorizationRuleImpl implements AuthorizationRule {

    private final AuthorizationQueryPort authorizationQueryPort;
    private final CatalogPort catalogPort;

    public AuthorizationRuleImpl(AuthorizationQueryPort authorizationQueryPort, CatalogPort catalogPort) {
        this.authorizationQueryPort = authorizationQueryPort;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final ExternalIdentity identity, final PermissionCode permission,
                         final AuthorizationScopeType scopeType, final UUID scopeId) {
        if (identity == null) {
            throw UnauthorizedException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_152.getCode()));
        }
        if (!authorizationQueryPort.hasPermission(identity, permission, scopeType, scopeId)) {
            throw ForbiddenException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()));
        }
    }
}

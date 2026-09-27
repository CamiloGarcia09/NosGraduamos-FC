package co.edu.uco.application.usecase.validator.authorization.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationValidationContext;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;

public final class AuthorizationPermissionRule extends RuleValidator<AuthorizationValidationContext> {

    public AuthorizationPermissionRule(CatalogPort catalogPort, AuthorizationQueryPort authorizationQueryPort) {
        super(catalogPort,
                context -> authorizationQueryPort.hasPermission(context.identity(), context.permission(),
                        context.scopeType(), context.scopeId()),
                MessageCatalogCodeEnum.FUN_153,
                ForbiddenException::buildUserException);
    }
}

package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.application.usecase.validator.authorization.rule.AuthorizationPermissionRule;
import co.edu.uco.application.usecase.validator.authorization.rule.ExternalIdentityRequiredRule;

import java.util.List;
import java.util.UUID;

public final class AuthorizationCompositeValidator extends CompositeValidator<AuthorizationValidationContext> {

    public AuthorizationCompositeValidator(AuthorizationQueryPort authorizationQueryPort, CatalogPort catalogPort) {
        super(List.of(
                forIdentity(new ExternalIdentityRequiredRule(catalogPort)),
                new AuthorizationPermissionRule(catalogPort, authorizationQueryPort)
        ), catalogPort);
    }

    public void validate(ExternalIdentity identity, PermissionCode permission,
                         AuthorizationScopeType scopeType, UUID scopeId) {
        super.validate(new AuthorizationValidationContext(identity, permission, scopeType, scopeId));
    }

    private static Validator<AuthorizationValidationContext> forIdentity(
            Validator<ExternalIdentity> validator) {
        return context -> validator.validate(context.identity());
    }
}

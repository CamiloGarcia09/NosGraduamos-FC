package co.edu.uco.application.usecase.validator.authorization.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.impl.NotNullSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;

public final class ExternalIdentityRequiredRule extends RuleValidator<ExternalIdentity> {

    public ExternalIdentityRequiredRule(CatalogPort catalogPort) {
        super(catalogPort, new NotNullSpecification<>(), MessageCatalogCodeEnum.FUN_152,
                UnauthorizedException::buildUserException);
    }
}

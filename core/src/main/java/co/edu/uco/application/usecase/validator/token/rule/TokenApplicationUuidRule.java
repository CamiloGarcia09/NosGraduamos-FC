package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class TokenApplicationUuidRule extends RuleValidator<TokenValidationContext> {

    public TokenApplicationUuidRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(TokenValidationContext::applicationId, new ValidUuidSpecification()),
                MessageCatalogCodeEnum.FUN_038);
    }
}
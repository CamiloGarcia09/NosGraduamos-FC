package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidDateSpecification;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class TokenExpirationDateValidRule extends RuleValidator<TokenValidationContext> {

    public TokenExpirationDateValidRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(context -> context.dto().getExpirationDate(), new ValidDateSpecification()),
                MessageCatalogCodeEnum.FUN_039);
    }
}
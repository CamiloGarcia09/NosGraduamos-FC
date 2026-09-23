package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ExpirationDateSpecification;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

import java.time.Clock;

import static co.edu.uco.crosscutting.helpers.UtilDate.nowUtc;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;

public final class TokenExpirationNotPassedRule extends RuleValidator<TokenValidationContext> {

    public TokenExpirationNotPassedRule(CatalogPort catalogPort, Clock clock) {
        super(catalogPort,
                Specifications.field(context -> parseDate(context.dto().getExpirationDate()),
                        new ExpirationDateSpecification(nowUtc(clock))),
                MessageCatalogCodeEnum.FUN_037);
    }
}
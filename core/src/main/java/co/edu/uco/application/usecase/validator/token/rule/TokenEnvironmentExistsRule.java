package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class TokenEnvironmentExistsRule extends RuleValidator<TokenValidationContext> {

    public TokenEnvironmentExistsRule(CatalogPort catalogPort, EnvironmentRepository environmentRepository) {
        super(catalogPort,
                Specifications.field(context -> context.dto().getEnvironmentId(),
                        environmentId -> environmentRepository.findById(environmentId).isPresent()),
                MessageCatalogCodeEnum.FUN_035);
    }
}
package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class TokenApplicationBelongsEnvironmentRule extends RuleValidator<TokenValidationContext> {

    public TokenApplicationBelongsEnvironmentRule(CatalogPort catalogPort, EnvironmentRepository environmentRepository) {
        super(catalogPort,
                context -> {
                    var environment = environmentRepository.findById(context.dto().getEnvironmentId());
                    return environment.isEmpty()
                            || environment.get().getApplication().getId().equals(getUUIDFromString(context.applicationId()));
                },
                MessageCatalogCodeEnum.FUN_036);
    }
}
package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ConflictException;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class SelectActiveContextEnvironmentBelongsApplicationRule
        extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextEnvironmentBelongsApplicationRule(final CatalogPort catalogPort,
                                                                final EnvironmentRepository environmentRepository) {
        super(catalogPort,
                context -> belongsToApplication(context, environmentRepository),
                MessageCatalogCodeEnum.FUN_160,
                ConflictException::buildUserException);
    }

    private static boolean belongsToApplication(final SelectActiveContextDTO context,
                                                final EnvironmentRepository environmentRepository) {
        if (context == null
                || !new ValidUuidSpecification().isSatisfiedBy(context.getApplicationId())
                || !new ValidUuidSpecification().isSatisfiedBy(context.getEnvironmentId())) {
            return false;
        }
        return environmentRepository.findById(context.getEnvironmentId())
                .map(environment -> getUUIDFromString(context.getApplicationId())
                        .equals(environment.getApplication().getId()))
                .orElse(true);
    }
}

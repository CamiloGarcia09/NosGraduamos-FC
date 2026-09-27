package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ConflictException;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class SelectActiveContextApplicationBelongsOrganizationRule
        extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextApplicationBelongsOrganizationRule(final CatalogPort catalogPort,
                                                                 final ApplicationRepository applicationRepository) {
        super(catalogPort,
                context -> belongsToOrganization(context, applicationRepository),
                MessageCatalogCodeEnum.FUN_159,
                ConflictException::buildUserException);
    }

    private static boolean belongsToOrganization(final SelectActiveContextDTO context,
                                                 final ApplicationRepository applicationRepository) {
        if (context == null
                || !new ValidUuidSpecification().isSatisfiedBy(context.getOrganizationId())
                || !new ValidUuidSpecification().isSatisfiedBy(context.getApplicationId())) {
            return false;
        }
        return applicationRepository.findById(context.getApplicationId())
                .map(application -> getUUIDFromString(context.getOrganizationId())
                        .equals(application.getOrganization().getId()))
                .orElse(true);
    }
}

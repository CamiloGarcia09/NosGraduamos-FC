package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.impl.TextRequiredSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class SelectActiveContextIdentifiersRequiredRule extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextIdentifiersRequiredRule(final CatalogPort catalogPort) {
        super(catalogPort,
                context -> context != null
                        && required(context.getOrganizationId())
                        && required(context.getApplicationId())
                        && required(context.getEnvironmentId()),
                MessageCatalogCodeEnum.FUN_155);
    }

    private static boolean required(final String value) {
        return new TextRequiredSpecification().isSatisfiedBy(value);
    }
}

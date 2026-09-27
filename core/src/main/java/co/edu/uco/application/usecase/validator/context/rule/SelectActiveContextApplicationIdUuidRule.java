package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class SelectActiveContextApplicationIdUuidRule extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextApplicationIdUuidRule(final CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(SelectActiveContextDTO::getApplicationId, new ValidUuidSpecification()),
                MessageCatalogCodeEnum.FUN_038);
    }
}

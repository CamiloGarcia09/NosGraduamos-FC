package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentApplicationIdUuidRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentApplicationIdUuidRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getApplicationId, new ValidUuidSpecification()),
                MessageCatalogCodeEnum.FUN_038);
    }
}
package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.TextRequiredSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentStateRequiredRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentStateRequiredRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getStateId, new TextRequiredSpecification()),
                MessageCatalogCodeEnum.FUN_160);
    }
}
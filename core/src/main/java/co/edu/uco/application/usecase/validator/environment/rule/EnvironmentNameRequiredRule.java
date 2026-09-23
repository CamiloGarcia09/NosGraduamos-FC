package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.TextRequiredSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentNameRequiredRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentNameRequiredRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getName, new TextRequiredSpecification()),
                MessageCatalogCodeEnum.FUN_154);
    }
}
package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.TextRequiredSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentTypeRequiredRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentTypeRequiredRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getTypeId, new TextRequiredSpecification()),
                MessageCatalogCodeEnum.FUN_158);
    }
}
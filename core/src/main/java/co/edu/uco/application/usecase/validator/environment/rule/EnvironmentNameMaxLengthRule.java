package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.TextMaxLengthSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentNameMaxLengthRule extends RuleValidator<CreateEnvironmentDTO> {

    private static final int NAME_MAX_LENGTH = 50;

    public EnvironmentNameMaxLengthRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getName, new TextMaxLengthSpecification(NAME_MAX_LENGTH)),
                MessageCatalogCodeEnum.FUN_155);
    }
}
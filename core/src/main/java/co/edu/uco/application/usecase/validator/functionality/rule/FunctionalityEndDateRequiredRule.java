package co.edu.uco.application.usecase.validator.functionality.rule;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.TextRequiredSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class FunctionalityEndDateRequiredRule extends RuleValidator<CreateFunctionalityDTO> {

    public FunctionalityEndDateRequiredRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateFunctionalityDTO::getEndDate, new TextRequiredSpecification()),
                MessageCatalogCodeEnum.FUN_167);
    }
}
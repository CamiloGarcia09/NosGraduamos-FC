package co.edu.uco.application.usecase.validator.functionality.rule;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidDateSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class FunctionalityStartDateValidRule extends RuleValidator<CreateFunctionalityDTO> {

    public FunctionalityStartDateValidRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(CreateFunctionalityDTO::getStartDate, new ValidDateSpecification()),
                MessageCatalogCodeEnum.FUN_039);
    }
}
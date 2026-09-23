package co.edu.uco.application.usecase.validator.functionality.rule;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.DateRangeOrderSpecification;
import co.edu.uco.application.usecase.validator.specification.impl.DateTimeRange;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;

public final class FunctionalityDateRangeOrderRule extends RuleValidator<CreateFunctionalityDTO> {

    public FunctionalityDateRangeOrderRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(
                        functionality -> new DateTimeRange(parseDate(functionality.getStartDate()),
                                parseDate(functionality.getEndDate())),
                        new DateRangeOrderSpecification()),
                MessageCatalogCodeEnum.FUN_173);
    }
}
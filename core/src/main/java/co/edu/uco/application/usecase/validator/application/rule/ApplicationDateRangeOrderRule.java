package co.edu.uco.application.usecase.validator.application.rule;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.DateRangeOrderSpecification;
import co.edu.uco.application.usecase.validator.specification.impl.DateTimeRange;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;

public final class ApplicationDateRangeOrderRule extends RuleValidator<CreateApplicationDTO> {

    public ApplicationDateRangeOrderRule(CatalogPort catalogPort) {
        super(catalogPort,
                Specifications.field(
                        application -> new DateTimeRange(parseDate(application.getStartDate()),
                                parseDate(application.getEndDate())),
                        new DateRangeOrderSpecification()),
                MessageCatalogCodeEnum.FUN_173);
    }
}
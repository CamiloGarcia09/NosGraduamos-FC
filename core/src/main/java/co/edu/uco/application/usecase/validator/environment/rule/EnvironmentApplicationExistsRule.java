package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentApplicationExistsRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentApplicationExistsRule(CatalogPort catalogPort, ApplicationRepository applicationRepository) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getApplicationId, applicationRepository::existsById),
                MessageCatalogCodeEnum.FUN_157);
    }
}
package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentNameDuplicatedRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentNameDuplicatedRule(CatalogPort catalogPort, EnvironmentRepository environmentRepository) {
        super(catalogPort,
                environment -> !environmentRepository.existsByNameAndApplicationId(environment.getName(),
                        environment.getApplicationId()),
                MessageCatalogCodeEnum.FUN_162);
    }
}
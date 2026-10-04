package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentTypeDuplicatedRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentTypeDuplicatedRule(CatalogPort catalogPort, EnvironmentRepository environmentRepository) {
        super(catalogPort,
                environment -> !environmentRepository.existsByApplicationIdAndTypeId(environment.getApplicationId(),
                        environment.getTypeId()),
                MessageCatalogCodeEnum.FUN_178);
    }
}

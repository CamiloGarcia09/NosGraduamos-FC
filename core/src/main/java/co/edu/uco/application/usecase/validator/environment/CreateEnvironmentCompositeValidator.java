package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import org.springframework.stereotype.Component;

import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentApplicationExistsRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentApplicationIdRequiredRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentApplicationIdUuidRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentNameDuplicatedRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentNameMaxLengthRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentNameRequiredRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentStateExistsRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentStateRequiredRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentTypeExistsRule;
import co.edu.uco.application.usecase.validator.environment.rule.EnvironmentTypeRequiredRule;

import java.util.List;

@Component
public final class CreateEnvironmentCompositeValidator extends CompositeValidator<CreateEnvironmentDTO> {

    public CreateEnvironmentCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository,
                                               EnvironmentRepository environmentRepository) {
        super(List.of(
                new EnvironmentNameRequiredRule(catalogPort),
                new EnvironmentNameMaxLengthRule(catalogPort),
                new EnvironmentApplicationIdRequiredRule(catalogPort),
                new EnvironmentApplicationIdUuidRule(catalogPort),
                new EnvironmentApplicationExistsRule(catalogPort, applicationRepository),
                new EnvironmentTypeRequiredRule(catalogPort),
                new EnvironmentTypeExistsRule(catalogPort, recordExistsCatalogPort),
                new EnvironmentStateRequiredRule(catalogPort),
                new EnvironmentStateExistsRule(catalogPort, recordExistsCatalogPort),
                new EnvironmentNameDuplicatedRule(catalogPort, environmentRepository)
        ), catalogPort);
    }
}
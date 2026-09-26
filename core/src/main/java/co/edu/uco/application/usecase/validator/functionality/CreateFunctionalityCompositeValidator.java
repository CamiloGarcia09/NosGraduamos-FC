package co.edu.uco.application.usecase.validator.functionality;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import org.springframework.stereotype.Component;

import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityApplicationExistsRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityApplicationIdRequiredRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityApplicationIdUuidRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityDateRangeOrderRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityEndDateRequiredRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityEndDateValidRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityNameDuplicatedRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityNameMaxLengthRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityNameRequiredRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityStartDateRequiredRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityStartDateValidRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityStateExistsRule;
import co.edu.uco.application.usecase.validator.functionality.rule.FunctionalityStateRequiredRule;

import java.util.List;

@Component
public final class CreateFunctionalityCompositeValidator extends CompositeValidator<CreateFunctionalityDTO> {

    public CreateFunctionalityCompositeValidator(CatalogPort catalogPort,
                                                 RecordExistsCatalogPort recordExistsCatalogPort,
                                                 ApplicationRepository applicationRepository,
                                                 FunctionalityRepository functionalityRepository) {
        super(List.of(
                new FunctionalityNameRequiredRule(catalogPort),
                new FunctionalityNameMaxLengthRule(catalogPort),
                new FunctionalityApplicationIdRequiredRule(catalogPort),
                new FunctionalityApplicationIdUuidRule(catalogPort),
                new FunctionalityApplicationExistsRule(catalogPort, applicationRepository),
                new FunctionalityStartDateRequiredRule(catalogPort),
                new FunctionalityStartDateValidRule(catalogPort),
                new FunctionalityEndDateRequiredRule(catalogPort),
                new FunctionalityEndDateValidRule(catalogPort),
                new FunctionalityDateRangeOrderRule(catalogPort),
                new FunctionalityStateRequiredRule(catalogPort),
                new FunctionalityStateExistsRule(catalogPort, recordExistsCatalogPort),
                new FunctionalityNameDuplicatedRule(catalogPort, functionalityRepository)
        ), catalogPort);
    }
}
package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import org.springframework.stereotype.Component;

import co.edu.uco.application.usecase.validator.application.rule.ApplicationDateRangeOrderRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationEndDateRequiredRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationEndDateValidRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationLanguageExistsRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationLanguageRequiredRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationNameDuplicatedRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationNameMaxLengthRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationNameRequiredRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationStartDateRequiredRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationStartDateValidRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationStateExistsRule;
import co.edu.uco.application.usecase.validator.application.rule.ApplicationStateRequiredRule;

import java.util.List;

@Component
public final class CreateApplicationCompositeValidator extends CompositeValidator<CreateApplicationDTO> {

    public CreateApplicationCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository) {
        super(List.of(
                new ApplicationNameRequiredRule(catalogPort),
                new ApplicationNameMaxLengthRule(catalogPort),
                new ApplicationLanguageRequiredRule(catalogPort),
                new ApplicationLanguageExistsRule(catalogPort, recordExistsCatalogPort),
                new ApplicationStartDateRequiredRule(catalogPort),
                new ApplicationStartDateValidRule(catalogPort),
                new ApplicationEndDateRequiredRule(catalogPort),
                new ApplicationEndDateValidRule(catalogPort),
                new ApplicationDateRangeOrderRule(catalogPort),
                new ApplicationStateRequiredRule(catalogPort),
                new ApplicationStateExistsRule(catalogPort, recordExistsCatalogPort),
                new ApplicationNameDuplicatedRule(catalogPort, applicationRepository)
        ), catalogPort);
    }
}
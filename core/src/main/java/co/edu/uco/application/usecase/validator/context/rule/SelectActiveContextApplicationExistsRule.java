package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.NotFoundException;

public final class SelectActiveContextApplicationExistsRule extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextApplicationExistsRule(final CatalogPort catalogPort,
                                                    final ApplicationRepository applicationRepository) {
        super(catalogPort,
                Specifications.field(SelectActiveContextDTO::getApplicationId,
                        applicationId -> new ValidUuidSpecification().isSatisfiedBy(applicationId)
                                && applicationRepository.findById(applicationId).isPresent()),
                MessageCatalogCodeEnum.FUN_157,
                NotFoundException::buildUserException);
    }
}

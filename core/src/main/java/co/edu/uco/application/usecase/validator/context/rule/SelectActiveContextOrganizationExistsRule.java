package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.ValidUuidSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.NotFoundException;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class SelectActiveContextOrganizationExistsRule extends RuleValidator<SelectActiveContextDTO> {

    public SelectActiveContextOrganizationExistsRule(final CatalogPort catalogPort,
                                                     final OrganizationRepository organizationRepository) {
        super(catalogPort,
                Specifications.field(SelectActiveContextDTO::getOrganizationId,
                        organizationId -> new ValidUuidSpecification().isSatisfiedBy(organizationId)
                                && organizationRepository.findById(getUUIDFromString(organizationId)).isPresent()),
                MessageCatalogCodeEnum.FUN_156,
                NotFoundException::buildUserException);
    }
}

package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

public final class CreateOrganizationUniqueNameRuleImpl implements CreateOrganizationUniqueNameRule {

    private final OrganizationRepository organizationRepository;
    private final CatalogPort catalogPort;

    public CreateOrganizationUniqueNameRuleImpl(final OrganizationRepository organizationRepository,
                                                final CatalogPort catalogPort) {
        this.organizationRepository = organizationRepository;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final CreateOrganizationDTO organizationDTO) {
        if (organizationRepository.findByName(organizationDTO.getName()).isPresent()) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_149.getCode()));
        }
    }
}

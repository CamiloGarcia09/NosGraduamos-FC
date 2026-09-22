package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;

public final class CreateOrganizationCompositeValidator {

    private final CatalogPort catalogPort;
    private final CreateOrganizationNameRule nameRule;
    private final CreateOrganizationUniqueNameRule uniqueNameRule;

    public CreateOrganizationCompositeValidator(final CatalogPort catalogPort,
                                                final CreateOrganizationNameRule nameRule,
                                                final CreateOrganizationUniqueNameRule uniqueNameRule) {
        this.catalogPort = catalogPort;
        this.nameRule = nameRule;
        this.uniqueNameRule = uniqueNameRule;
    }

    public void validate(final CreateOrganizationDTO organizationDTO) {
        if (isNullObject(organizationDTO)) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode()));
        }
        nameRule.validate(organizationDTO);
        uniqueNameRule.validate(organizationDTO);
    }
}

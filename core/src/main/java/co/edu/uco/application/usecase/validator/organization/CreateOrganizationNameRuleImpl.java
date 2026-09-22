package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

public final class CreateOrganizationNameRuleImpl implements CreateOrganizationNameRule {

    private static final int NAME_MAX_LENGTH = 50;
    private final CatalogPort catalogPort;

    public CreateOrganizationNameRuleImpl(final CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final CreateOrganizationDTO organizationDTO) {
        if (isEmptyOrNull(organizationDTO.getName())) {
            throw businessRule(MessageCatalogCodeEnum.FUN_147);
        }
        if (organizationDTO.getName().length() > NAME_MAX_LENGTH) {
            throw businessRule(MessageCatalogCodeEnum.FUN_148);
        }
    }

    private BusinessRuleException businessRule(final MessageCatalogCodeEnum code) {
        return BusinessRuleException.buildUserException(catalogPort.getMessage(code.getCode()));
    }
}

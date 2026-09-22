package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class CreateApplicationOrganizationExistsRuleImpl
        implements CreateApplicationOrganizationExistsRule {

    private final OrganizationRepository organizationRepository;
    private final UUIDValidator uuidValidator;
    private final CatalogPort catalogPort;

    public CreateApplicationOrganizationExistsRuleImpl(final OrganizationRepository organizationRepository,
                                                       final UUIDValidator uuidValidator,
                                                       final CatalogPort catalogPort) {
        this.organizationRepository = organizationRepository;
        this.uuidValidator = uuidValidator;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final CreateApplicationDTO applicationDTO) {
        if (isEmptyOrNull(applicationDTO.getOrganizationId())) {
            throw businessRule(MessageCatalogCodeEnum.FUN_150);
        }
        uuidValidator.validate(applicationDTO.getOrganizationId());
        if (organizationRepository.findById(getUUIDFromString(applicationDTO.getOrganizationId())).isEmpty()) {
            throw businessRule(MessageCatalogCodeEnum.FUN_151);
        }
    }

    private BusinessRuleException businessRule(final MessageCatalogCodeEnum code) {
        return BusinessRuleException.buildUserException(catalogPort.getMessage(code.getCode()));
    }
}

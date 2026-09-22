package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;

public interface CreateOrganizationUniqueNameRule {
    void validate(CreateOrganizationDTO organizationDTO);
}

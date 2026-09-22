package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;

public interface CreateApplicationOrganizationExistsRule {
    void validate(CreateApplicationDTO applicationDTO);
}

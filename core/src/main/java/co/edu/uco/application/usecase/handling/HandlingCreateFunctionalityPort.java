package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface HandlingCreateFunctionalityPort {
    void createFunctionality(CreateFunctionalityDTO functionalityDTO, ExternalIdentity identity);
}
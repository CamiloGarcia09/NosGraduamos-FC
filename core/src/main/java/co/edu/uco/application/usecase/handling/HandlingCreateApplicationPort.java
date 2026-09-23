package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface HandlingCreateApplicationPort {
    void createApplication(CreateApplicationDTO applicationDTO, ExternalIdentity identity);
}
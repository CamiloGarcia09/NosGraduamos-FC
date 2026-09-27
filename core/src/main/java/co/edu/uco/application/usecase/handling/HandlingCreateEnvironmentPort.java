package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface HandlingCreateEnvironmentPort {
    void createEnvironment(CreateEnvironmentDTO environmentDTO, ExternalIdentity identity);
}
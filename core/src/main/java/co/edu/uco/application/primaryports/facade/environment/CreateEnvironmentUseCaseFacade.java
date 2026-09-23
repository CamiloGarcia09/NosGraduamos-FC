package co.edu.uco.application.primaryports.facade.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface CreateEnvironmentUseCaseFacade {
    void execute(CreateEnvironmentDTO createEnvironmentDTO, ExternalIdentity identity);
}
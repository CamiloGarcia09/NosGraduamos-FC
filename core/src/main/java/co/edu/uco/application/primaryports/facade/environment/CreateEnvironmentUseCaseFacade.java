package co.edu.uco.application.primaryports.facade.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;

public interface CreateEnvironmentUseCaseFacade {
    void execute(CreateEnvironmentDTO createEnvironmentDTO);
}
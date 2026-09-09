package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;

public interface HandlingCreateEnvironmentPort {
    void createEnvironment(CreateEnvironmentDTO environmentDTO);
}
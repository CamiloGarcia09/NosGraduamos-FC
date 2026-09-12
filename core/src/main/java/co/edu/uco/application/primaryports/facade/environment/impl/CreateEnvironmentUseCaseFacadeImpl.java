package co.edu.uco.application.primaryports.facade.environment.impl;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.primaryports.facade.environment.CreateEnvironmentUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingCreateEnvironmentPort;
import org.springframework.stereotype.Component;

@Component
public final class CreateEnvironmentUseCaseFacadeImpl implements CreateEnvironmentUseCaseFacade {

    private final HandlingCreateEnvironmentPort handlingCreateEnvironmentPort;

    public CreateEnvironmentUseCaseFacadeImpl(HandlingCreateEnvironmentPort handlingCreateEnvironmentPort) {
        this.handlingCreateEnvironmentPort = handlingCreateEnvironmentPort;
    }

    @Override
    public void execute(CreateEnvironmentDTO createEnvironmentDTO) {
        handlingCreateEnvironmentPort.createEnvironment(createEnvironmentDTO);
    }
}
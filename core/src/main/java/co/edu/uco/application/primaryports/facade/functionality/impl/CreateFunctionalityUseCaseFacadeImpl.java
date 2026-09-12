package co.edu.uco.application.primaryports.facade.functionality.impl;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.primaryports.facade.functionality.CreateFunctionalityUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingCreateFunctionalityPort;
import org.springframework.stereotype.Component;

@Component
public final class CreateFunctionalityUseCaseFacadeImpl implements CreateFunctionalityUseCaseFacade {

    private final HandlingCreateFunctionalityPort handlingCreateFunctionalityPort;

    public CreateFunctionalityUseCaseFacadeImpl(HandlingCreateFunctionalityPort handlingCreateFunctionalityPort) {
        this.handlingCreateFunctionalityPort = handlingCreateFunctionalityPort;
    }

    @Override
    public void execute(CreateFunctionalityDTO createFunctionalityDTO) {
        handlingCreateFunctionalityPort.createFunctionality(createFunctionalityDTO);
    }
}
package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingFindMessageByCodeAndEnvironmentPort;
import org.springframework.stereotype.Component;

@Component
public final class FindMessageByCodeAndEnvironmentUseCaseFacadeImpl implements FindMessageByCodeAndEnvironmentUseCaseFacade {
    private final HandlingFindMessageByCodeAndEnvironmentPort handlingFindMessageByCodeAndEnvironmentPort;
    public FindMessageByCodeAndEnvironmentUseCaseFacadeImpl(
            HandlingFindMessageByCodeAndEnvironmentPort handlingFindMessageByCodeAndEnvironmentPort) {
        this.handlingFindMessageByCodeAndEnvironmentPort = handlingFindMessageByCodeAndEnvironmentPort;
    }
    @Override
    public MessageDTO execute(String messageCode, String environmentId) {
        return handlingFindMessageByCodeAndEnvironmentPort.execute(messageCode, environmentId);
    }
}
package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingFindMessageByCodeAndEnvironmentPort;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import org.springframework.stereotype.Component;

@Component
public final class FindMessageByCodeAndEnvironmentUseCaseFacadeImpl implements FindMessageByCodeAndEnvironmentUseCaseFacade {
    private final HandlingFindMessageByCodeAndEnvironmentPort handlingFindMessageByCodeAndEnvironmentPort;
    public FindMessageByCodeAndEnvironmentUseCaseFacadeImpl(
            HandlingFindMessageByCodeAndEnvironmentPort handlingFindMessageByCodeAndEnvironmentPort) {
        this.handlingFindMessageByCodeAndEnvironmentPort = handlingFindMessageByCodeAndEnvironmentPort;
    }
    @Override
    public MessageDTO execute(String messageCode, MessageAccessContext context) {
        return handlingFindMessageByCodeAndEnvironmentPort.execute(messageCode, context);
    }
}

package co.edu.uco.core.application.facade.token.impl;

import co.edu.uco.core.application.facade.token.FindEnvironmentIdTokenUseCaseFacade;
import co.edu.uco.core.domain.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import org.springframework.stereotype.Component;

@Component
public final class FindEnvironmentIdTokenUseCaseFacadeImpl implements FindEnvironmentIdTokenUseCaseFacade {
    private final HandlingFindEnvironmentIdTokenPort handlingFindEnvironmentIdTokenPort;
    public FindEnvironmentIdTokenUseCaseFacadeImpl(HandlingFindEnvironmentIdTokenPort handlingFindEnvironmentIdTokenPort) {
        this.handlingFindEnvironmentIdTokenPort = handlingFindEnvironmentIdTokenPort;
    }
    @Override
    public String execute(String token) {
        return handlingFindEnvironmentIdTokenPort.execute(token);
    }
}
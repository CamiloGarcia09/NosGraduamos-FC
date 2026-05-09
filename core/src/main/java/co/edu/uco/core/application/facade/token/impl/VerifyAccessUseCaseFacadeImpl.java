package co.edu.uco.core.application.facade.token.impl;

import co.edu.uco.core.application.facade.token.VerifyAccessUseCaseFacade;
import co.edu.uco.core.domain.usecase.handling.HandlingVerifyAccessPort;
import org.springframework.stereotype.Component;

@Component
public final class VerifyAccessUseCaseFacadeImpl implements VerifyAccessUseCaseFacade {
    private final HandlingVerifyAccessPort handlingVerifyAccessPort;
    public VerifyAccessUseCaseFacadeImpl(HandlingVerifyAccessPort handlingVerifyAccessPort) {
        this.handlingVerifyAccessPort = handlingVerifyAccessPort;
    }
    @Override
    public boolean execute(String token) {
        return handlingVerifyAccessPort.verifyAccess(token);
    }
}
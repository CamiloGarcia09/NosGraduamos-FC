package co.edu.uco.application.primaryports.facade.context.impl;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.primaryports.facade.context.ActiveContextUseCaseFacade;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;

import java.util.List;

public final class ActiveContextUseCaseFacadeImpl implements ActiveContextUseCaseFacade {

    private final HandlingActiveContextPort handlingActiveContextPort;

    public ActiveContextUseCaseFacadeImpl(final HandlingActiveContextPort handlingActiveContextPort) {
        this.handlingActiveContextPort = handlingActiveContextPort;
    }

    @Override
    public List<AvailableContextDTO> findAvailableContexts(final ExternalIdentity identity) {
        return handlingActiveContextPort.findAvailableContexts(identity);
    }

    @Override
    public ActiveContextDTO findActiveContext(final ExternalIdentity identity) {
        return handlingActiveContextPort.findActiveContext(identity);
    }

    @Override
    public ActiveContextDTO selectActiveContext(final SelectActiveContextDTO context,
                                                final ExternalIdentity identity) {
        return handlingActiveContextPort.selectActiveContext(context, identity);
    }
}

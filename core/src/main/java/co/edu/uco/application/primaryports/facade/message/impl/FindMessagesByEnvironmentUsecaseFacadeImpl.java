package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessagesByEnvironmentUsecaseFacade;
import co.edu.uco.application.primaryports.facade.page.impl.SimplePageFacadeImpl;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.usecase.handling.HandlingFindMessageEnvironmentPort;
import org.springframework.stereotype.Component;

@Component
public final class FindMessagesByEnvironmentUsecaseFacadeImpl implements FindMessagesByEnvironmentUsecaseFacade {
    private final HandlingFindMessageEnvironmentPort handlingFindMessageEnvironmentPort;
    private final SimplePageFacadeImpl simplePageFacadeImpl;
    public FindMessagesByEnvironmentUsecaseFacadeImpl(
            HandlingFindMessageEnvironmentPort handlingFindMessageEnvironmentPort,
            SimplePageFacadeImpl simplePageFacadeImpl) {
        this.handlingFindMessageEnvironmentPort = handlingFindMessageEnvironmentPort;
        this.simplePageFacadeImpl = simplePageFacadeImpl;
    }
    public SimplePage<MessageDTO> execute(String environmentId, PageRequestDTO pageDTO) {
        var simplePageRequest = simplePageFacadeImpl.execute(pageDTO);
        return handlingFindMessageEnvironmentPort.execute(environmentId, simplePageRequest);
    }
}
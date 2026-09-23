package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.primaryports.facade.message.CreateMessageUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import co.edu.uco.infraestructure.primaryadapters.CreateMessageController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.ENVIRONMENT_ID_ATTRIBUTE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;

@RestController
final class CreateMessageControllerImpl implements CreateMessageController {

    private final CreateMessageUseCaseFacade createMessageUseCaseFacade;
    private final PresenterPort<String> restPresenter;

    public CreateMessageControllerImpl(CreateMessageUseCaseFacade createMessageUseCaseFacade,
                                       PresenterPort<String> restPresenter) {
        this.createMessageUseCaseFacade = createMessageUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void createMessage(CreateMessageDTO createMessageDTO, HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) {
        createMessageUseCaseFacade.execute(createMessageDTO, accessContext(httpServletRequest));
        restPresenter.presentRestSuccess(List.of("Mensaje creado exitosamente"), httpServletRequest, httpServletResponse);
    }

    private static MessageAccessContext accessContext(final HttpServletRequest request) {
        return new MessageAccessContext(
                (String) request.getAttribute(ENVIRONMENT_ID_ATTRIBUTE),
                (ExternalIdentity) request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE));
    }
}

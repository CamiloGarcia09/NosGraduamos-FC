package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.primaryports.facade.message.CreateMessageUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CreateMessageController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.ENVIRONMENT_ID_ATTRIBUTE;

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
        var environmentId = (String) httpServletRequest.getAttribute(ENVIRONMENT_ID_ATTRIBUTE);
        createMessageUseCaseFacade.execute(createMessageDTO, environmentId);
        restPresenter.presentRestSuccess(List.of("Mensaje creado exitosamente"), httpServletRequest, httpServletResponse);
    }
}

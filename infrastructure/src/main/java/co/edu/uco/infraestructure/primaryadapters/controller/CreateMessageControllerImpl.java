package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.primaryports.facade.message.CreateMessageUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CreateMessageController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        createMessageUseCaseFacade.execute(createMessageDTO);
        restPresenter.presentRestSuccess(List.of("Mensaje creado exitosamente"), httpServletRequest, httpServletResponse);
    }
}

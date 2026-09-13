package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.primaryports.facade.application.CreateApplicationUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CreateApplicationController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
final class CreateApplicationControllerImpl implements CreateApplicationController {

    private final CreateApplicationUseCaseFacade createApplicationUseCaseFacade;
    private final PresenterPort<String> restPresenter;

    public CreateApplicationControllerImpl(CreateApplicationUseCaseFacade createApplicationUseCaseFacade,
                                           PresenterPort<String> restPresenter) {
        this.createApplicationUseCaseFacade = createApplicationUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void createApplication(CreateApplicationDTO createApplicationDTO, HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) {
        createApplicationUseCaseFacade.execute(createApplicationDTO);
        restPresenter.presentRestSuccess(List.of("Aplicación creada exitosamente"), httpServletRequest, httpServletResponse);
    }
}
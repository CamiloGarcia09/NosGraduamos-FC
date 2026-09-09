package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.primaryports.facade.functionality.CreateFunctionalityUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CreateFunctionalityController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
final class CreateFunctionalityControllerImpl implements CreateFunctionalityController {

    private final CreateFunctionalityUseCaseFacade createFunctionalityUseCaseFacade;
    private final PresenterPort<String> restPresenter;

    public CreateFunctionalityControllerImpl(CreateFunctionalityUseCaseFacade createFunctionalityUseCaseFacade,
                                             PresenterPort<String> restPresenter) {
        this.createFunctionalityUseCaseFacade = createFunctionalityUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void createFunctionality(CreateFunctionalityDTO createFunctionalityDTO, HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) {
        createFunctionalityUseCaseFacade.execute(createFunctionalityDTO);
        restPresenter.presentRestSuccess(List.of("Funcionalidad creada exitosamente"), httpServletRequest, httpServletResponse);
    }
}
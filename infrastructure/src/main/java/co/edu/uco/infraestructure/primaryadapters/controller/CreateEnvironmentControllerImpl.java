package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.primaryports.facade.environment.CreateEnvironmentUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.infraestructure.primaryadapters.CreateEnvironmentController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;

@RestController
final class CreateEnvironmentControllerImpl implements CreateEnvironmentController {

    private final CreateEnvironmentUseCaseFacade createEnvironmentUseCaseFacade;
    private final PresenterPort<String> restPresenter;

    public CreateEnvironmentControllerImpl(CreateEnvironmentUseCaseFacade createEnvironmentUseCaseFacade,
                                           PresenterPort<String> restPresenter) {
        this.createEnvironmentUseCaseFacade = createEnvironmentUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void createEnvironment(CreateEnvironmentDTO createEnvironmentDTO, HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) {
        createEnvironmentUseCaseFacade.execute(
                createEnvironmentDTO, identity(httpServletRequest));
        restPresenter.presentRestSuccess(List.of("Entorno creado exitosamente"), httpServletRequest, httpServletResponse);
    }

    private static ExternalIdentity identity(final HttpServletRequest request) {
        return (ExternalIdentity) request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE);
    }
}
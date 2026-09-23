package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.primaryports.facade.context.ActiveContextUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.infraestructure.primaryadapters.MeContextController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;

@RestController
final class MeContextControllerImpl implements MeContextController {

    private final ActiveContextUseCaseFacade activeContextUseCaseFacade;
    private final PresenterPort<AvailableContextDTO> availableContextPresenter;
    private final PresenterPort<ActiveContextDTO> activeContextPresenter;

    public MeContextControllerImpl(final ActiveContextUseCaseFacade activeContextUseCaseFacade,
                                   final PresenterPort<AvailableContextDTO> availableContextPresenter,
                                   final PresenterPort<ActiveContextDTO> activeContextPresenter) {
        this.activeContextUseCaseFacade = activeContextUseCaseFacade;
        this.availableContextPresenter = availableContextPresenter;
        this.activeContextPresenter = activeContextPresenter;
    }

    @Override
    public void getAvailableContexts(final HttpServletRequest request, final HttpServletResponse response) {
        List<AvailableContextDTO> contexts = activeContextUseCaseFacade.findAvailableContexts(identity(request));
        availableContextPresenter.presentRestSuccess(contexts, request, response);
    }

    @Override
    public void getActiveContext(final HttpServletRequest request, final HttpServletResponse response) {
        ActiveContextDTO context = activeContextUseCaseFacade.findActiveContext(identity(request));
        activeContextPresenter.presentRestSuccess(List.of(context), request, response);
    }

    @Override
    public void selectActiveContext(final SelectActiveContextDTO context, final HttpServletRequest request,
                                    final HttpServletResponse response) {
        ActiveContextDTO selected = activeContextUseCaseFacade.selectActiveContext(context, identity(request));
        activeContextPresenter.presentRestSuccess(List.of(selected), request, response);
    }

    private ExternalIdentity identity(final HttpServletRequest request) {
        return (ExternalIdentity) request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE);
    }
}

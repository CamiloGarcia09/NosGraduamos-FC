package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.facade.catalog.FindCatalogUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CatalogController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class CatalogControllerImpl implements CatalogController {

    private final FindCatalogUseCaseFacade findCatalogUseCaseFacade;
    private final PresenterPort<CatalogItemDTO> restPresenter;

    public CatalogControllerImpl(FindCatalogUseCaseFacade findCatalogUseCaseFacade,
                                 PresenterPort<CatalogItemDTO> restPresenter) {
        this.findCatalogUseCaseFacade = findCatalogUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void getApplications(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findApplications();
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getEnvironmentsByApplication(String applicationId, HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findEnvironmentsByApplication(applicationId);
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getFunctionalitiesByApplication(String applicationId, HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findFunctionalitiesByApplication(applicationId);
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getMessageTypes(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findMessageTypes();
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getMessageCategories(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findMessageCategories();
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getMessageStates(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findMessageStates();
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }

    @Override
    public void getMessageEnvironmentStates(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var list = findCatalogUseCaseFacade.findMessageEnvironmentStates();
        restPresenter.presentRestSuccess(list, httpServletRequest, httpServletResponse);
    }
}

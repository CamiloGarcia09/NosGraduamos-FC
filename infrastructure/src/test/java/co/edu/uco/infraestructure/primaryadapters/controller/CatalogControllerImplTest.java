package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.facade.catalog.FindCatalogUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogControllerImplTest {

    @Mock
    private FindCatalogUseCaseFacade findCatalogUseCaseFacade;
    @Mock
    private PresenterPort<CatalogItemDTO> restPresenter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private CatalogControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new CatalogControllerImpl(findCatalogUseCaseFacade, restPresenter);
    }

    @Test
    void getApplications_presentsFacadeResult() {
        List<CatalogItemDTO> list = List.of(CatalogItemDTO.create("1", "App"));
        when(findCatalogUseCaseFacade.findApplications()).thenReturn(list);

        controller.getApplications(request, response);

        verify(findCatalogUseCaseFacade).findApplications();
        verify(restPresenter).presentRestSuccess(eq(list), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getEnvironmentsByApplication_presentsFacadeResult() {
        controller.getEnvironmentsByApplication("app-1", request, response);

        verify(findCatalogUseCaseFacade).findEnvironmentsByApplication("app-1");
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getFunctionalitiesByApplication_presentsFacadeResult() {
        controller.getFunctionalitiesByApplication("app-1", request, response);

        verify(findCatalogUseCaseFacade).findFunctionalitiesByApplication("app-1");
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getMessageTypes_presentsFacadeResult() {
        controller.getMessageTypes(request, response);

        verify(findCatalogUseCaseFacade).findMessageTypes();
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getMessageCategories_presentsFacadeResult() {
        controller.getMessageCategories(request, response);

        verify(findCatalogUseCaseFacade).findMessageCategories();
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getMessageStates_presentsFacadeResult() {
        controller.getMessageStates(request, response);

        verify(findCatalogUseCaseFacade).findMessageStates();
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void getMessageEnvironmentStates_presentsFacadeResult() {
        controller.getMessageEnvironmentStates(request, response);

        verify(findCatalogUseCaseFacade).findMessageEnvironmentStates();
        verify(restPresenter).presentRestSuccess(any(), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
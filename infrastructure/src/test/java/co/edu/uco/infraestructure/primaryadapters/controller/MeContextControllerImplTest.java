package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.primaryports.facade.context.ActiveContextUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeContextControllerImplTest {

    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

    @Mock
    private ActiveContextUseCaseFacade facade;
    @Mock
    private PresenterPort<AvailableContextDTO> availableContextPresenter;
    @Mock
    private PresenterPort<ActiveContextDTO> activeContextPresenter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private MeContextControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new MeContextControllerImpl(facade, availableContextPresenter, activeContextPresenter);
        when(request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE)).thenReturn(IDENTITY);
    }

    @Test
    void getAvailableContexts_propagatesIdentityAndPresentsResult() {
        List<AvailableContextDTO> contexts = List.of(AvailableContextDTO.builder()
                .organization(CatalogItemDTO.create("org-1", "Organization"))
                .application(CatalogItemDTO.create("app-1", "Application"))
                .environment(CatalogItemDTO.create("env-1", "Environment"))
                .build());
        when(facade.findAvailableContexts(IDENTITY)).thenReturn(contexts);

        controller.getAvailableContexts(request, response);

        verify(facade).findAvailableContexts(IDENTITY);
        verify(availableContextPresenter).presentRestSuccess(contexts, request, response);
    }

    @Test
    void getActiveContext_propagatesIdentityAndPresentsSingletonResult() {
        ActiveContextDTO context = ActiveContextDTO.builder().organizationId("org-1").build();
        when(facade.findActiveContext(IDENTITY)).thenReturn(context);

        controller.getActiveContext(request, response);

        verify(facade).findActiveContext(IDENTITY);
        verify(activeContextPresenter).presentRestSuccess(List.of(context), request, response);
    }

    @Test
    void selectActiveContext_propagatesIdentityAndPresentsSingletonResult() {
        SelectActiveContextDTO selection = SelectActiveContextDTO.builder()
                .organizationId("org-1").applicationId("app-1").environmentId("env-1").build();
        ActiveContextDTO selected = ActiveContextDTO.builder().organizationId("org-1").build();
        when(facade.selectActiveContext(selection, IDENTITY)).thenReturn(selected);

        controller.selectActiveContext(selection, request, response);

        verify(facade).selectActiveContext(selection, IDENTITY);
        verify(activeContextPresenter).presentRestSuccess(List.of(selected), request, response);
    }
}

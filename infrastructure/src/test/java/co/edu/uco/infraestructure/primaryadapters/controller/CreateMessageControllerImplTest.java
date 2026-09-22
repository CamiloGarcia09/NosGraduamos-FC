package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.primaryports.facade.message.CreateMessageUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageControllerImplTest {

    @Mock
    private CreateMessageUseCaseFacade createMessageUseCaseFacade;
    @Mock
    private PresenterPort<String> restPresenter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private CreateMessageControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new CreateMessageControllerImpl(createMessageUseCaseFacade, restPresenter);
    }

    @Test
    void createMessage_usesAuthenticatedEnvironmentAndPresentsSuccess() {
        CreateMessageDTO dto = CreateMessageDTO.builder().environmentId("env-body").build();
        when(request.getAttribute("environmentId")).thenReturn("env-token");

        controller.createMessage(dto, request, response);

        verify(createMessageUseCaseFacade).execute(dto, "env-token");
        verify(restPresenter).presentRestSuccess(
                List.of("Mensaje creado exitosamente"), request, response);
    }
}

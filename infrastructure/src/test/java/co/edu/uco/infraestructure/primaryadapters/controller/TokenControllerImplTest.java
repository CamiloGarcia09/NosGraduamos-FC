package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.primaryports.facade.token.CreateTokenUseCaseFacade;
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
class TokenControllerImplTest {

    @Mock
    private CreateTokenUseCaseFacade createTokenUseCaseFacade;
    @Mock
    private PresenterPort<String> restPresenter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private TokenControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new TokenControllerImpl(createTokenUseCaseFacade, restPresenter);
    }

    @Test
    void createToken_executesFacadeAndPresentsResult() {
        CreateTokenDTO dto = new CreateTokenDTO("2026-01-01", "env-1");
        when(createTokenUseCaseFacade.execute(dto, "app-id")).thenReturn("token-1");

        controller.createToken(dto, "app-id", request, response);

        verify(restPresenter).presentRestSuccess(List.of("token-1"), request, response);
    }
}
package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.primaryports.facade.token.CreateTokenUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.TokenController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
final class TokenControllerImpl implements TokenController {
    private final CreateTokenUseCaseFacade createTokenUseCaseFacade;
    private final PresenterPort<String> restPresenter;

    public TokenControllerImpl(CreateTokenUseCaseFacade createTokenUseCaseFacade, PresenterPort<String> restPresenter) {
        this.createTokenUseCaseFacade = createTokenUseCaseFacade;
        this.restPresenter = restPresenter;
    }

    @Override
    public void createToken(
            CreateTokenDTO tokenDTO,
            String id,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        var result = createTokenUseCaseFacade.execute(tokenDTO, id);
        restPresenter.presentRestSuccess(List.of(result), httpServletRequest, httpServletResponse);
    }
}

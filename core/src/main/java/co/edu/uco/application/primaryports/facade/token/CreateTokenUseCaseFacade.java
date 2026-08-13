package co.edu.uco.application.primaryports.facade.token;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;

public interface CreateTokenUseCaseFacade {
    String execute(CreateTokenDTO tokenDTO, String application);
}

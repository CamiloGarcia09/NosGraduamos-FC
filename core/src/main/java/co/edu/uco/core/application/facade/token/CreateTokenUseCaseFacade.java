package co.edu.uco.core.application.facade.token;

import co.edu.uco.core.application.dto.token.CreateTokenDTO;

public interface CreateTokenUseCaseFacade {
    String execute(CreateTokenDTO tokenDTO, String application);
}

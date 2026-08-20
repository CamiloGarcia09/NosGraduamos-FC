package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;

public interface CreateMessageUseCaseFacade {
    void execute(CreateMessageDTO createMessageDTO);
}

package co.edu.uco.core.application.facade.message;

import co.edu.uco.core.application.dto.message.MessageDTO;

public interface FindMessageByCodeAndEnvironmentUseCaseFacade {
    MessageDTO execute(String messageCode, String environmentId);
}
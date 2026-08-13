package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;

public interface FindMessageByCodeAndEnvironmentUseCaseFacade {
    MessageDTO execute(String messageCode, String environmentId);
}
package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;

public interface FindMessageByCodeAndEnvironmentUseCaseFacade {
    MessageDTO execute(String messageCode, MessageAccessContext context);
}

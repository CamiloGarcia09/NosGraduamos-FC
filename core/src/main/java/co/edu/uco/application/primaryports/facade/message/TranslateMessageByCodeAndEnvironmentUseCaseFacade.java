package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;

public interface TranslateMessageByCodeAndEnvironmentUseCaseFacade {
    TranslatedMessageDTO execute(String messageCode, MessageAccessContext context,
                                 String sourceLanguage, String targetLanguage);
}

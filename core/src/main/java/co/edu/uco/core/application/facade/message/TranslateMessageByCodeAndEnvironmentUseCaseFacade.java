package co.edu.uco.core.application.facade.message;

import co.edu.uco.core.application.dto.message.TranslatedMessageDTO;

public interface TranslateMessageByCodeAndEnvironmentUseCaseFacade {
    TranslatedMessageDTO execute(String messageCode, String environmentId, String sourceLanguage, String targetLanguage);
}

package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;

public interface TranslateMessageByCodeAndEnvironmentUseCaseFacade {
    TranslatedMessageDTO execute(String messageCode, String environmentId, String sourceLanguage, String targetLanguage);
}

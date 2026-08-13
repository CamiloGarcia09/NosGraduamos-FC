package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;

public interface HandlingTranslateMessageByCodeAndEnvironmentPort {
    TranslatedMessageDTO execute(String messageCode, String environmentId, String sourceLanguage, String targetLanguage);
}

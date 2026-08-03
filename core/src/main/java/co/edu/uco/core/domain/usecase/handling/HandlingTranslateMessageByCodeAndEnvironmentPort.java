package co.edu.uco.core.domain.usecase.handling;

import co.edu.uco.core.application.dto.message.TranslatedMessageDTO;

public interface HandlingTranslateMessageByCodeAndEnvironmentPort {
    TranslatedMessageDTO execute(String messageCode, String environmentId, String sourceLanguage, String targetLanguage);
}

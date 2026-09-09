package co.edu.uco.application.secondaryports.translation;

import co.edu.uco.application.secondaryports.entity.MessageTranslationResponseData;

import java.util.Optional;

public interface MessageTranslationCachePort {
    Optional<MessageTranslationResponseData> findTranslation(String messageCode, String environmentId, String sourceLanguage, String targetLanguage);
    void saveTranslation(String messageCode, String environmentId, String sourceLanguage, String targetLanguage, MessageTranslationResponseData translation);
}
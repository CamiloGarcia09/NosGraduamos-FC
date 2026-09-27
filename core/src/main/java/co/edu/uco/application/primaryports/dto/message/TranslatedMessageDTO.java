package co.edu.uco.application.primaryports.dto.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@EqualsAndHashCode
@ToString
public final class TranslatedMessageDTO {

    private final String code;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final String originalTitle;
    private final String originalContent;
    private final String translatedTitle;
    private final String translatedContent;
    private final String type;
    private final String category;
    private final String application;
    private final String functionality;
    private final String translationProvider;
    private final String translationModel;
    private final long translationElapsedMs;
    private final boolean dynamicTranslation;

    public TranslatedMessageDTO(
            String code,
            String sourceLanguage,
            String targetLanguage,
            String originalTitle,
            String originalContent,
            String translatedTitle,
            String translatedContent,
            String type,
            String category,
            String application,
            String functionality,
            String translationProvider,
            String translationModel,
            long translationElapsedMs,
            boolean dynamicTranslation
    ) {
        this.code = trim(code);
        this.sourceLanguage = trim(sourceLanguage);
        this.targetLanguage = trim(targetLanguage);
        this.originalTitle = trim(originalTitle);
        this.originalContent = trim(originalContent);
        this.translatedTitle = trim(translatedTitle);
        this.translatedContent = trim(translatedContent);
        this.type = trim(type);
        this.category = trim(category);
        this.application = trim(application);
        this.functionality = trim(functionality);
        this.translationProvider = trim(translationProvider);
        this.translationModel = trim(translationModel);
        this.translationElapsedMs = translationElapsedMs;
        this.dynamicTranslation = dynamicTranslation;
    }

    public static TranslatedMessageDTO create(
            String code,
            String sourceLanguage,
            String targetLanguage,
            String originalTitle,
            String originalContent,
            String translatedTitle,
            String translatedContent,
            String type,
            String category,
            String application,
            String functionality,
            String translationProvider,
            String translationModel,
            long translationElapsedMs
    ) {
        return new TranslatedMessageDTO(
                code,
                sourceLanguage,
                targetLanguage,
                originalTitle,
                originalContent,
                translatedTitle,
                translatedContent,
                type,
                category,
                application,
                functionality,
                translationProvider,
                translationModel,
                translationElapsedMs,
                true
        );
    }
}

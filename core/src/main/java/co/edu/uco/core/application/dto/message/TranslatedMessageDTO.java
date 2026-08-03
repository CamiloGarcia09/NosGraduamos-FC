package co.edu.uco.core.application.dto.message;

import static co.edu.uco.utils.helper.UtilText.trim;

public record TranslatedMessageDTO(
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

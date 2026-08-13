package co.edu.uco.application.secondaryports.entity;

import lombok.Getter;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
public final class MessageTranslationResponseData {
    private final String translatedTitle;
    private final String translatedContent;
    private final String provider;
    private final String model;
    private final long elapsedMillis;

    private MessageTranslationResponseData(
            String translatedTitle,
            String translatedContent,
            String provider,
            String model,
            long elapsedMillis
    ) {
        this.translatedTitle = trim(translatedTitle);
        this.translatedContent = trim(translatedContent);
        this.provider = trim(provider);
        this.model = trim(model);
        this.elapsedMillis = elapsedMillis;
    }

    public static MessageTranslationResponseData create(
            String translatedTitle,
            String translatedContent,
            String provider,
            String model,
            long elapsedMillis
    ) {
        return new MessageTranslationResponseData(translatedTitle, translatedContent, provider, model, elapsedMillis);
    }
}

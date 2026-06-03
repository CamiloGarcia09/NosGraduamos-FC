package co.edu.uco.core.application.facade.message.impl;

import co.edu.uco.core.application.dto.message.TranslatedMessageDTO;
import co.edu.uco.core.application.facade.message.TranslateMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.core.domain.usecase.handling.HandlingTranslateMessageByCodeAndEnvironmentPort;
import org.springframework.stereotype.Component;

@Component
public final class TranslateMessageByCodeAndEnvironmentUseCaseFacadeImpl
        implements TranslateMessageByCodeAndEnvironmentUseCaseFacade {
    private final HandlingTranslateMessageByCodeAndEnvironmentPort handlingTranslateMessageByCodeAndEnvironmentPort;

    public TranslateMessageByCodeAndEnvironmentUseCaseFacadeImpl(
            HandlingTranslateMessageByCodeAndEnvironmentPort handlingTranslateMessageByCodeAndEnvironmentPort
    ) {
        this.handlingTranslateMessageByCodeAndEnvironmentPort = handlingTranslateMessageByCodeAndEnvironmentPort;
    }

    @Override
    public TranslatedMessageDTO execute(
            String messageCode,
            String environmentId,
            String sourceLanguage,
            String targetLanguage
    ) {
        return handlingTranslateMessageByCodeAndEnvironmentPort.execute(
                messageCode,
                environmentId,
                sourceLanguage,
                targetLanguage
        );
    }
}

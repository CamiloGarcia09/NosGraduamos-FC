package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.primaryports.facade.message.TranslateMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingTranslateMessageByCodeAndEnvironmentPort;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
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
            MessageAccessContext context,
            String sourceLanguage,
            String targetLanguage
    ) {
        return handlingTranslateMessageByCodeAndEnvironmentPort.execute(
                messageCode,
                context,
                sourceLanguage,
                targetLanguage
        );
    }
}

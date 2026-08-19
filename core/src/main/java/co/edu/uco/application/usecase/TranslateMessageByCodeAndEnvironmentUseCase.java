package co.edu.uco.application.usecase;

import co.edu.uco.application.common.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.secondaryports.entity.MessageTranslationRequestData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.translation.MessageTranslationPort;
import co.edu.uco.application.usecase.handling.HandlingTranslateMessageByCodeAndEnvironmentPort;
import co.edu.uco.application.usecase.validator.message.FindMessageCodeValidator;
import co.edu.uco.application.usecase.validator.message.TargetLanguageValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Component
public final class TranslateMessageByCodeAndEnvironmentUseCase
        implements HandlingTranslateMessageByCodeAndEnvironmentPort {
    private static final String DEFAULT_SOURCE_LANGUAGE = "auto";

    private final MessageCatalogStrategy messageCatalogStrategy;
    private final MessageTranslationPort messageTranslationPort;
    private final FindMessageCodeValidator findMessageCodeValidator;
    private final TargetLanguageValidator targetLanguageValidator;
    private final LoggingPort log;

    public TranslateMessageByCodeAndEnvironmentUseCase(
            MessageCatalogStrategy messageCatalogStrategy,
            MessageTranslationPort messageTranslationPort,
            FindMessageCodeValidator findMessageCodeValidator,
            TargetLanguageValidator targetLanguageValidator,
            LoggingPortFactory loggerFactory
    ) {
        this.messageCatalogStrategy = messageCatalogStrategy;
        this.messageTranslationPort = messageTranslationPort;
        this.findMessageCodeValidator = findMessageCodeValidator;
        this.targetLanguageValidator = targetLanguageValidator;
        this.log = loggerFactory.getLogger(TranslateMessageByCodeAndEnvironmentUseCase.class);
    }

    @Override
    public TranslatedMessageDTO execute(
            String messageCode,
            String environmentId,
            String sourceLanguage,
            String targetLanguage
    ) {
        try {
            findMessageCodeValidator.validate(messageCode);
            targetLanguageValidator.validate(targetLanguage);

            var normalizedSourceLanguage = isEmptyOrNull(sourceLanguage)
                    ? DEFAULT_SOURCE_LANGUAGE
                    : trim(sourceLanguage);
            var messageData = messageCatalogStrategy
                    .getMessageByCodeAndEnvironment(messageCode, environmentId)
                    .orElseThrow(() -> {
                        var errorMessage = String.format(
                                messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()),
                                messageCode,
                                environmentId
                        );
                        return BusinessException.buildUserException(errorMessage);
                    });
            var type = messageData.getType().getName();
            var category = messageData.getCategory().getName();
            var functionality = messageData.getFunctionality().getName();
            var requestData = MessageTranslationRequestData.create(
                    messageData.getCode(),
                    normalizedSourceLanguage,
                    targetLanguage,
                    messageData.getTitle(),
                    messageData.getContent(),
                    type,
                    category,
                    messageData.getApplication(),
                    functionality
            );
            var translationData = messageTranslationPort.translate(requestData);
            return TranslatedMessageDTO.create(
                    messageData.getCode(),
                    normalizedSourceLanguage,
                    targetLanguage,
                    messageData.getTitle(),
                    messageData.getContent(),
                    translationData.getTranslatedTitle(),
                    translationData.getTranslatedContent(),
                    type,
                    category,
                    messageData.getApplication(),
                    functionality,
                    translationData.getProvider(),
                    translationData.getModel(),
                    translationData.getElapsedMillis()
            );
        } catch (CrossWordsException exception) {
            throw exception;
        } catch (Exception exception) {
            var errorMessage = String.format(messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()), messageCode, environmentId);
            log.error(errorMessage, exception);
            throw BusinessException.buildUserException(errorMessage);
        }
    }
}
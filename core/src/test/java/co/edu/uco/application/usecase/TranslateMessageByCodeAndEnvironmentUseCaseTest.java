package co.edu.uco.application.usecase;

import co.edu.uco.application.common.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.MessageTranslationRequestData;
import co.edu.uco.application.secondaryports.entity.MessageTranslationResponseData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.translation.MessageTranslationPort;
import co.edu.uco.application.usecase.validator.message.FindMessageCodeValidator;
import co.edu.uco.application.usecase.validator.message.TargetLanguageValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslateMessageByCodeAndEnvironmentUseCaseTest {

    @Mock
    private MessageCatalogStrategy messageCatalogStrategy;
    @Mock
    private MessageTranslationPort messageTranslationPort;
    @Mock
    private FindMessageCodeValidator findMessageCodeValidator;
    @Mock
    private TargetLanguageValidator targetLanguageValidator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private TranslateMessageByCodeAndEnvironmentUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(TranslateMessageByCodeAndEnvironmentUseCase.class)).thenReturn(log);
        useCase = new TranslateMessageByCodeAndEnvironmentUseCase(
                messageCatalogStrategy, messageTranslationPort, findMessageCodeValidator,
                targetLanguageValidator, loggerFactory);
    }

    private MessageData messageWithData() {
        MessageData message = new MessageData();
        message.setCode("CODE");
        message.setTitle("Titulo");
        message.setContent("Contenido");
        message.setType(new MessageTypeData(java.util.UUID.randomUUID(), "info"));
        message.setCategory(new MessageCategoryData(java.util.UUID.randomUUID(), "general"));
        message.setApplication("app");
        message.setFunctionality(new co.edu.uco.application.secondaryports.entity.FunctionalityData(
                java.util.UUID.randomUUID(), "func", null, null, null));
        return message;
    }

    @Test
    void execute_translatesMessageSuccessfully() {
        MessageData message = messageWithData();
        when(messageCatalogStrategy.getMessageByCodeAndEnvironment("CODE", "env"))
                .thenReturn(Optional.of(message));
        MessageTranslationResponseData response = MessageTranslationResponseData.create(
                "Translated Title", "Translated Content", "ollama", "llama3.2", 120L);
        when(messageTranslationPort.translate(org.mockito.ArgumentMatchers.any(MessageTranslationRequestData.class)))
                .thenReturn(response);

        TranslatedMessageDTO result = useCase.execute("CODE", "env", "", "en");

        assertThat(result.code()).isEqualTo("CODE");
        assertThat(result.sourceLanguage()).isEqualTo("auto");
        assertThat(result.targetLanguage()).isEqualTo("en");
        assertThat(result.translatedTitle()).isEqualTo("Translated Title");
        assertThat(result.translationProvider()).isEqualTo("ollama");
        assertThat(result.translationElapsedMs()).isEqualTo(120L);
        assertThat(result.dynamicTranslation()).isTrue();
    }

    @Test
    void execute_usesTrimmedSourceLanguage_whenProvided() {
        MessageData message = messageWithData();
        when(messageCatalogStrategy.getMessageByCodeAndEnvironment("CODE", "env"))
                .thenReturn(Optional.of(message));
        MessageTranslationResponseData response = MessageTranslationResponseData.create(
                "t", "c", "ollama", "m", 1L);
        when(messageTranslationPort.translate(org.mockito.ArgumentMatchers.any(MessageTranslationRequestData.class)))
                .thenReturn(response);

        TranslatedMessageDTO result = useCase.execute("CODE", "env", "  es ", "en");

        assertThat(result.sourceLanguage()).isEqualTo("es");
    }

    @Test
    void execute_throwsBusinessException_whenMessageNotFound() {
        when(messageCatalogStrategy.getMessageByCodeAndEnvironment("CODE", "env"))
                .thenReturn(Optional.empty());
        when(messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()))
                .thenReturn("Message %s not found in environment %s");

        assertThatThrownBy(() -> useCase.execute("CODE", "env", "es", "en"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage())
                        .isEqualTo("Message CODE not found in environment env"));
    }

    @Test
    void execute_throwsBusinessException_whenTranslationFails() {
        MessageData message = messageWithData();
        when(messageCatalogStrategy.getMessageByCodeAndEnvironment("CODE", "env"))
                .thenReturn(Optional.of(message));
        when(messageTranslationPort.translate(org.mockito.ArgumentMatchers.any(MessageTranslationRequestData.class)))
                .thenThrow(new IllegalStateException("translation down"));
        when(messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()))
                .thenReturn("Message %s not found in environment %s");

        assertThatThrownBy(() -> useCase.execute("CODE", "env", "es", "en"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage())
                        .isEqualTo("Message CODE not found in environment env"));

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(log).error(org.mockito.ArgumentMatchers.eq("Message CODE not found in environment env"), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void execute_propagatesValidatorException_whenMessageCodeIsInvalid() {
        doThrow(BusinessRuleException.buildUserException("Invalid message code"))
                .when(findMessageCodeValidator).validate("CODE");

        assertThatThrownBy(() -> useCase.execute("CODE", "env", "es", "en"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid message code"));
    }

    @Test
    void execute_propagatesValidatorException_whenTargetLanguageIsInvalid() {
        doThrow(BusinessRuleException.buildUserException("Invalid target language"))
                .when(targetLanguageValidator).validate("en");

        assertThatThrownBy(() -> useCase.execute("CODE", "env", "es", "en"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid target language"));

        verify(findMessageCodeValidator).validate("CODE");
    }
}
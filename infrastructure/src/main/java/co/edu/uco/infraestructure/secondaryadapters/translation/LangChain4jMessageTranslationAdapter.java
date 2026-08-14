package co.edu.uco.infraestructure.secondaryadapters.translation;

import co.edu.uco.application.secondaryports.entity.MessageTranslationRequestData;
import co.edu.uco.application.secondaryports.entity.MessageTranslationResponseData;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.translation.MessageTranslationPort;
import co.edu.uco.application.secondaryports.vault.VaultPort;
import co.edu.uco.infraestructure.config.TranslationAiProperties;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Component
public class LangChain4jMessageTranslationAdapter implements MessageTranslationPort {

    private final LoggingPort log;
    private final VaultPort vault;
    private final CatalogPort catalogPort;
    private final TranslationAiProperties properties;
    private final ObjectMapper objectMapper;
    private ChatModel chatModel;

    public LangChain4jMessageTranslationAdapter(
            VaultPort vault, CatalogPort catalogPort, TranslationAiProperties properties,
            ObjectMapper objectMapper, LoggingPortFactory loggerFactory
    ) {
        this.log = loggerFactory.getLogger(LangChain4jMessageTranslationAdapter.class);
        this.vault = vault;
        this.catalogPort = catalogPort;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    private static final String PROVIDER_OPEN_AI = "openai";

    @Override
    public MessageTranslationResponseData translate(MessageTranslationRequestData requestData) {
        if (!properties.isEnabled()) {
            throw BusinessException.buildUserException(catalogPort.getMessage("FUN_046"));
        }
        if (isOpenAiProvider() && isEmptyOrNull(properties.getApiKey())) {
            throw BusinessException.buildUserException(catalogPort.getMessage("FUN_047"));
        }

        var startedAt = System.nanoTime();
        try {
            var response = model().chat(buildChatRequest(requestData));
            var output = response.aiMessage().text();
            var translated = objectMapper.readValue(output, TranslationModelResponse.class);
            var elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
            log.info(
                    catalogPort.getMessage("TCH_036"),
                    providerName(),
                    properties.getModelName(),
                    requestData.getCode(),
                    requestData.getTargetLanguage(),
                    elapsedMillis
            );
            return MessageTranslationResponseData.create(
                    translated.translatedTitle(),
                    translated.translatedContent(),
                    providerName(),
                    properties.getModelName(),
                    elapsedMillis
            );
        } catch (Exception exception) {
            log.error(
                    catalogPort.getMessage("TCH_037"),
                    providerName(),
                    properties.getModelName(),
                    requestData.getCode(),
                    requestData.getTargetLanguage(),
                    exception
            );
            throw BusinessException.buildUserException(catalogPort.getMessage("FUN_048"));
        }
    }

    private ChatModel model() {
        if (chatModel == null) {
            chatModel = isOpenAiProvider() ? openAiModel() : ollamaModel();
        }
        return chatModel;
    }

    private ChatModel openAiModel() {
        var builder = OpenAiChatModel.builder()
                .apiKey(properties.getApiKey())
                .modelName(properties.getModelName())
                .temperature(properties.getTemperature())
                .maxRetries(properties.getMaxRetries())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        if (!isEmptyOrNull(properties.getBaseUrl())) {
            builder.baseUrl(properties.getBaseUrl());
        }

        return builder.build();
    }

    private ChatModel ollamaModel() {
        return OllamaChatModel.builder()
                .baseUrl(properties.getBaseUrl())
                .modelName(properties.getModelName())
                .temperature(properties.getTemperature())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .build();
    }

    private boolean isOpenAiProvider() {
        return PROVIDER_OPEN_AI.equalsIgnoreCase(trim(properties.getProvider()));
    }

    private String providerName() {
        return isOpenAiProvider()
                ? catalogPort.getMessage("FUN_058")
                : catalogPort.getMessage("FUN_059").formatted(vault.getSecretValue("TRANSLATION-AI-PROVIDER"));
    }

    private ChatRequest buildChatRequest(MessageTranslationRequestData requestData) {
        var responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchema.builder()
                        .name(catalogPort.getMessage("FUN_055"))
                        .rootElement(JsonObjectSchema.builder()
                                .addStringProperty(catalogPort.getMessage("FUN_056"))
                                .addStringProperty(catalogPort.getMessage("FUN_057"))
                                .required(catalogPort.getMessage("FUN_056"), catalogPort.getMessage("FUN_057"))
                                .build())
                        .build())
                .build();

        return ChatRequest.builder()
                .messages(UserMessage.from(buildPrompt(requestData)))
                .responseFormat(responseFormat)
                .build();
    }

    private String buildPrompt(MessageTranslationRequestData requestData) {
        return catalogPort.getMessage("FUN_054").formatted(
                requestData.getCode(),
                requestData.getSourceLanguage(),
                requestData.getTargetLanguage(),
                requestData.getApplication(),
                requestData.getFunctionality(),
                requestData.getType(),
                requestData.getCategory(),
                trim(requestData.getTitle()),
                trim(requestData.getContent())
        );
    }

    private record TranslationModelResponse(String translatedTitle, String translatedContent) {
    }
}

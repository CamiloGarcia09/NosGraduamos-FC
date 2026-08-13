package co.edu.uco.infrastructure.adapter.secondary.translation;

import co.edu.uco.core.domain.data.MessageTranslationRequestData;
import co.edu.uco.core.domain.data.MessageTranslationResponseData;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.translation.MessageTranslationPort;
import co.edu.uco.core.domain.port.out.vault.VaultPort;
import co.edu.uco.infrastructure.configuration.TranslationAiProperties;
import co.edu.uco.utils.exception.BusinessException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;
import static co.edu.uco.utils.helper.UtilText.trim;

@Slf4j
@Component
public class LangChain4jMessageTranslationAdapter implements MessageTranslationPort {

    private final VaultPort vault;
    private final CatalogPort catalogPort;
    private final TranslationAiProperties properties;
    private final ObjectMapper objectMapper;
    private ChatModel chatModel;

    public LangChain4jMessageTranslationAdapter(
            VaultPort vault, CatalogPort catalogPort, TranslationAiProperties properties,
            ObjectMapper objectMapper
    ) {
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
                    "Dynamic translation completed with provider={} model={} code={} targetLanguage={} elapsedMs={}",
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
                    "Dynamic translation failed with provider={} model={} code={} targetLanguage={}",
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
        return isOpenAiProvider() ? "langchain4j-open-ai" : "langchain4j-" + vault.getSecretValue("TRANSLATION-AI-PROVIDER");
    }

    private ChatRequest buildChatRequest(MessageTranslationRequestData requestData) {
        var responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchema.builder()
                        .name("MessageTranslation")
                        .rootElement(JsonObjectSchema.builder()
                                .addStringProperty("translatedTitle")
                                .addStringProperty("translatedContent")
                                .required("translatedTitle", "translatedContent")
                                .build())
                        .build())
                .build();

        return ChatRequest.builder()
                .messages(UserMessage.from(buildPrompt(requestData)))
                .responseFormat(responseFormat)
                .build();
    }

    private String buildPrompt(MessageTranslationRequestData requestData) {
        return """
                Translate this catalog message dynamically.

                Rules:
                - Translate intent, tone, and colloquial meaning instead of word by word.
                - Keep placeholders unchanged: %%s, %%d, {}, {name}, ${value}.
                - Keep message codes, URLs, HTML tags, and technical identifiers unchanged.
                - Return only the requested JSON fields.

                Context:
                code: %s
                sourceLanguage: %s
                targetLanguage: %s
                application: %s
                functionality: %s
                type: %s
                category: %s

                Title:
                %s

                Content:
                %s
                """.formatted(
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

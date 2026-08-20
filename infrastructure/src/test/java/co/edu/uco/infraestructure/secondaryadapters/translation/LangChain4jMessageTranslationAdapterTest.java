package co.edu.uco.infraestructure.secondaryadapters.translation;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.MessageTranslationRequestData;
import co.edu.uco.application.secondaryports.entity.MessageTranslationResponseData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.vault.VaultPort;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.infraestructure.config.TranslationAiProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LangChain4jMessageTranslationAdapterTest {

    @Mock
    private VaultPort vaultPort;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private ChatResponse chatResponse;
    @Mock
    private AiMessage aiMessage;

    private final TranslationAiProperties properties = new TranslationAiProperties();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private LangChain4jMessageTranslationAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(LangChain4jMessageTranslationAdapter.class)).thenReturn(log);
        adapter = new LangChain4jMessageTranslationAdapter(vaultPort, catalogPort, properties, objectMapper, loggerFactory);
    }

    private MessageTranslationRequestData request() {
        return MessageTranslationRequestData.create(
                "MSG-001", "en", "es", "Hello", "World", "TEXT", "GENERAL", "app", "translation"
        );
    }

    private void injectChatModel(ChatModel model) throws Exception {
        Field field = LangChain4jMessageTranslationAdapter.class.getDeclaredField("chatModel");
        field.setAccessible(true);
        field.set(adapter, model);
    }

    private ChatModel chatModelReturning(ChatResponse response) {
        return new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                return response;
            }
        };
    }

    private ChatModel chatModelThrowing() {
        return new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                throw new RuntimeException("model down");
            }
        };
    }

    private void stubSchemaMessages() {
        when(catalogPort.getMessage("FUN_054")).thenReturn("%s");
        when(catalogPort.getMessage("FUN_055")).thenReturn("message");
        when(catalogPort.getMessage("FUN_056")).thenReturn("title");
        when(catalogPort.getMessage("FUN_057")).thenReturn("content");
    }

    @Test
    void translate_throwsBusinessException_whenTranslationDisabled() {
        properties.setEnabled(false);
        when(catalogPort.getMessage("FUN_046")).thenReturn("translation disabled");

        assertThatThrownBy(() -> adapter.translate(request()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage()).isEqualTo("translation disabled"));
    }

    @Test
    void translate_throwsBusinessException_whenOpenAiProviderWithoutApiKey() {
        properties.setEnabled(true);
        properties.setProvider("openai");
        properties.setApiKey(null);
        when(catalogPort.getMessage("FUN_047")).thenReturn("api key missing");

        assertThatThrownBy(() -> adapter.translate(request()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage()).isEqualTo("api key missing"));
    }

    @Test
    void translate_returnsTranslatedData_whenOpenAiProvider() throws Exception {
        properties.setEnabled(true);
        properties.setProvider("openai");
        properties.setApiKey("sk-test");
        properties.setModelName("gpt-4o-mini");
        injectChatModel(chatModelReturning(chatResponse));
        stubSchemaMessages();

        when(chatResponse.aiMessage()).thenReturn(aiMessage);
        when(aiMessage.text()).thenReturn("{\"translatedTitle\":\"Hola\",\"translatedContent\":\"Mundo\"}");
        when(catalogPort.getMessage("FUN_058")).thenReturn("OpenAI");
        when(catalogPort.getMessage("TCH_036")).thenReturn("translated");

        MessageTranslationResponseData result = adapter.translate(request());

        assertThat(result.getTranslatedTitle()).isEqualTo("Hola");
        assertThat(result.getTranslatedContent()).isEqualTo("Mundo");
        assertThat(result.getProvider()).isEqualTo("OpenAI");
        assertThat(result.getModel()).isEqualTo("gpt-4o-mini");
        assertThat(result.getElapsedMillis()).isGreaterThanOrEqualTo(0);
        verify(log).info(anyString(), anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void translate_logsAndThrows_whenModelCallFails() throws Exception {
        properties.setEnabled(true);
        properties.setProvider("openai");
        properties.setApiKey("sk-test");
        properties.setModelName("gpt-4o-mini");
        injectChatModel(chatModelThrowing());
        stubSchemaMessages();

        when(catalogPort.getMessage("FUN_058")).thenReturn("OpenAI");
        when(catalogPort.getMessage("FUN_048")).thenReturn("translation failed");
        when(catalogPort.getMessage("TCH_037")).thenReturn("tch037");

        assertThatThrownBy(() -> adapter.translate(request()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage()).isEqualTo("translation failed"));
        verify(log).error(anyString(), anyString(), anyString(), anyString(), anyString(), any(RuntimeException.class));
    }
}
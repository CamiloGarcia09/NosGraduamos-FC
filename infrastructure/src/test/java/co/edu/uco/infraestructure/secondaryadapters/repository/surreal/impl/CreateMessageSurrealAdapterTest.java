package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.application.secondaryports.entity.StatusMessageData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import com.surrealdb.Surreal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageSurrealAdapterTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private CatalogPort catalogPort;

    private CreateMessageSurrealAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateMessageSurrealAdapter.class)).thenReturn(log);
        CatalogPortStaticRef.set(catalogPort);
        adapter = new CreateMessageSurrealAdapter(surreal, loggerFactory);
    }

    @AfterEach
    void tearDown() {
        CatalogPortStaticRef.set(null);
    }

    private MessageData buildMessage() {
        MessageData message = MessageData.build();
        message.setId(UUID.randomUUID());
        message.setCode("MSG-001");
        message.setTitle("A valid title");
        message.setContent("A valid content");
        message.setApplication("App");

        MessageTypeData type = MessageTypeData.build();
        type.setName("message_type:TEXT");
        message.setType(type);

        MessageCategoryData category = MessageCategoryData.build();
        category.setName("GENERAL");
        message.setCategory(category);

        StatusMessageData status = StatusMessageData.build();
        status.setName("ACTIVE");
        message.setStatus(status);

        ApplicationData application = ApplicationData.build();
        FunctionalityData functionality = FunctionalityData.build();
        functionality.setName("Search");
        functionality.setApplication(application);
        message.setFunctionality(functionality);
        return message;
    }

    @Test
    void createMessage_persistsMessageAndMessageEnvironment() {
        MessageData message = buildMessage();

        adapter.createMessage(message, "env-1", "state-1");

        verify(surreal, times(2)).query(anyString());
        verify(log, times(2)).info(anyString(), anyString());
    }

    @Test
    void createMessage_throwsBusinessException_whenQueryFails() {
        when(catalogPort.getMessage(anyString())).thenReturn("msg");
        doThrow(new RuntimeException("db down")).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.createMessage(buildMessage(), "env-1", "state-1"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("msg"));
        verify(log).error(anyString(), any(RuntimeException.class));
    }
}
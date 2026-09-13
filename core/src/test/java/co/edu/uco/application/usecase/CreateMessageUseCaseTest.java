package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.CreateMessageRepository;
import co.edu.uco.application.usecase.validator.message.CreateMessageCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageUseCaseTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";
    private static final String FUNC_UUID = "123e4567-e89b-12d3-a456-426614175001";

    @Mock
    private CreateMessageRepository createMessageRepository;
    @Mock
    private CreateMessageCompositeValidator validator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateMessageUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateMessageUseCase.class)).thenReturn(log);
        useCase = new CreateMessageUseCase(createMessageRepository, validator, loggerFactory);
    }

    private CreateMessageDTO validDto() {
        return CreateMessageDTO.builder()
                .code("MSG-001")
                .title("A valid title")
                .content("A valid message content")
                .typeId("type-1")
                .categoryId("cat-1")
                .statusId("status-1")
                .applicationId(APP_UUID)
                .application("App")
                .functionalityId(FUNC_UUID)
                .environmentId("env-1")
                .messageEnvironmentStateId("state-1")
                .build();
    }

    @Test
    void createMessage_persistsMessageAndLogs() {
        CreateMessageDTO dto = validDto();

        useCase.createMessage(dto);

        verify(validator).validate(dto);
        verify(createMessageRepository).createMessage(any(), eq("env-1"), eq("state-1"));
        verify(log).info("Message created successfully with code: {}", "MSG-001");
    }

    @Test
    void createMessage_propagatesValidationError() {
        CreateMessageDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid message"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createMessage(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid message"));
        verifyNoInteractions(createMessageRepository);
    }

    @Test
    void createMessage_throwsCrossWordsException_whenRepositoryFails() {
        CreateMessageDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(createMessageRepository).createMessage(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createMessage(dto))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage())
                        .isEqualTo("Error al crear el mensaje"));
        verify(log).error(eq("Error creating message in repository"), any(RuntimeException.class));
    }

    @Test
    void createMessage_rethrowsCrossWordsExceptionFromRepository() {
        CreateMessageDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("conflict"))
                .when(createMessageRepository).createMessage(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createMessage(dto))
                .isInstanceOf(BusinessRuleException.class);
    }
}
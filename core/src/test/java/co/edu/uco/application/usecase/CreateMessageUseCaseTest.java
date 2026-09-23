package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.CreateMessageRepository;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.security.MessageEnvironmentResolver;
import co.edu.uco.application.usecase.validator.message.CreateMessageCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

    @Mock
    private CreateMessageRepository createMessageRepository;
    @Mock
    private CreateMessageCompositeValidator validator;
    @Mock
    private MessageEnvironmentResolver environmentResolver;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateMessageUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateMessageUseCase.class)).thenReturn(log);
        useCase = new CreateMessageUseCase(createMessageRepository, validator,
                environmentResolver, loggerFactory);
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

    private MessageAccessContext legacyContext() {
        return new MessageAccessContext("env-1", null);
    }

    private MessageAccessContext authenticatedContext() {
        return new MessageAccessContext(null, IDENTITY);
    }

    @Test
    void createMessage_resolvesLegacyEnvironmentWithMessageCreatePermissionAndPersists() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");

        useCase.createMessage(dto, context);

        verify(environmentResolver).resolve(context, PermissionCode.MESSAGE_CREATE);
        verify(validator).validate(dto, "env-1");
        verify(createMessageRepository).createMessage(any(), eq("env-1"), eq("state-1"));
        verify(log).info("Message created successfully with code: {}", "MSG-001");
    }

    @Test
    void createMessage_usesResolvedEnvironmentEvenWhenBodyEnvironmentDiffers() {
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId("env-body");
        MessageAccessContext context = authenticatedContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-auth");

        useCase.createMessage(dto, context);

        verify(environmentResolver).resolve(context, PermissionCode.MESSAGE_CREATE);
        verify(validator).validate(dto, "env-auth");
        verify(createMessageRepository).createMessage(any(), eq("env-auth"), eq("state-1"));
        verify(log).info("Message created successfully with code: {}", "MSG-001");
    }

    @Test
    void createMessage_propagatesForbiddenFromEnvironmentResolver() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = authenticatedContext();
        ForbiddenException failure = ForbiddenException.buildUserException("Permission denied");
        doThrow(failure).when(environmentResolver).resolve(context, PermissionCode.MESSAGE_CREATE);

        assertThatThrownBy(() -> useCase.createMessage(dto, context)).isSameAs(failure);
        verifyNoInteractions(validator, createMessageRepository);
    }

    @Test
    void createMessage_propagatesValidationError() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");
        doThrow(BusinessRuleException.buildUserException("Invalid message"))
                .when(validator).validate(dto, "env-1");

        assertThatThrownBy(() -> useCase.createMessage(dto, context))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid message"));
        verifyNoInteractions(createMessageRepository);
    }

    @Test
    void createMessage_throwsCrossWordsException_whenRepositoryFails() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");
        doThrow(new RuntimeException("db down")).when(createMessageRepository)
                .createMessage(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createMessage(dto, context))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage())
                        .isEqualTo("Error al crear el mensaje"));
        verify(log).error(eq("Error creating message in repository"), any(RuntimeException.class));
    }

    @Test
    void createMessage_rethrowsCrossWordsExceptionFromRepository() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");
        doThrow(BusinessRuleException.buildUserException("conflict"))
                .when(createMessageRepository).createMessage(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createMessage(dto, context))
                .isInstanceOf(BusinessRuleException.class);
    }
}

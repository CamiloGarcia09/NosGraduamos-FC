package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.entity.MessageData;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
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
    private static final String TYPE_UUID = "123e4567-e89b-12d3-a456-426614175010";
    private static final String CATEGORY_UUID = "123e4567-e89b-12d3-a456-426614175011";
    private static final String STATUS_UUID = "123e4567-e89b-12d3-a456-426614175012";
    private static final String MESSAGE_ENVIRONMENT_STATE_UUID = "123e4567-e89b-12d3-a456-426614175013";
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
                .typeId(TYPE_UUID)
                .categoryId(CATEGORY_UUID)
                .statusId(STATUS_UUID)
                .applicationId(APP_UUID)
                .application("App")
                .functionalityId(FUNC_UUID)
                .environmentId("env-1")
                .messageEnvironmentStateId(MESSAGE_ENVIRONMENT_STATE_UUID)
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
        verify(createMessageRepository).createMessage(any(), eq("env-1"), eq(MESSAGE_ENVIRONMENT_STATE_UUID));
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
        verify(createMessageRepository).createMessage(any(), eq("env-auth"), eq(MESSAGE_ENVIRONMENT_STATE_UUID));
        verify(log).info("Message created successfully with code: {}", "MSG-001");
    }

    @Test
    void createMessage_buildsCatalogEntitiesWithUuidIdsAndEmptyNames() {
        CreateMessageDTO dto = validDto();
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");
        ArgumentCaptor<MessageData> messageCaptor = ArgumentCaptor.forClass(MessageData.class);

        useCase.createMessage(dto, context);

        verify(createMessageRepository).createMessage(messageCaptor.capture(), eq("env-1"),
                eq(MESSAGE_ENVIRONMENT_STATE_UUID));
        MessageData message = messageCaptor.getValue();
        assertSoftly(softly -> {
            softly.assertThat(message.getId()).isNotNull();
            softly.assertThat(message.getType().getId()).isEqualTo(UUID.fromString(TYPE_UUID));
            softly.assertThat(message.getType().getName()).isEmpty();
            softly.assertThat(message.getCategory().getId()).isEqualTo(UUID.fromString(CATEGORY_UUID));
            softly.assertThat(message.getCategory().getName()).isEmpty();
            softly.assertThat(message.getStatus().getId()).isEqualTo(UUID.fromString(STATUS_UUID));
            softly.assertThat(message.getStatus().getName()).isEmpty();
            softly.assertThat(message.getFunctionality().getId()).isEqualTo(UUID.fromString(FUNC_UUID));
            softly.assertThat(message.getFunctionality().getName()).isEmpty();
            softly.assertThat(message.getFunctionality().getApplication().getId())
                    .isEqualTo(UUID.fromString(APP_UUID));
        });
    }

    @Test
    void createMessage_throwsCrossWordsException_whenCatalogReferenceIsNotUuid() {
        CreateMessageDTO dto = validDto();
        dto.setTypeId("not-a-uuid");
        MessageAccessContext context = legacyContext();
        when(environmentResolver.resolve(context, PermissionCode.MESSAGE_CREATE)).thenReturn("env-1");

        assertThatThrownBy(() -> useCase.createMessage(dto, context))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage())
                        .isEqualTo("The UUID to be converted has no valid format."));
        verifyNoInteractions(createMessageRepository);
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

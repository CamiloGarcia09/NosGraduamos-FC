package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.primaryports.facade.message.CreateMessageUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.ENVIRONMENT_ID_ATTRIBUTE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageControllerImplTest {

    @Mock
    private CreateMessageUseCaseFacade createMessageUseCaseFacade;
    @Mock
    private PresenterPort<String> restPresenter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private CreateMessageControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new CreateMessageControllerImpl(createMessageUseCaseFacade, restPresenter);
    }

    @Test
    void createMessage_buildsLegacyAccessContextAndPresentsSuccess() {
        CreateMessageDTO dto = CreateMessageDTO.builder().environmentId("env-body").build();
        when(request.getAttribute(ENVIRONMENT_ID_ATTRIBUTE)).thenReturn("env-token");
        when(request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE)).thenReturn(null);

        controller.createMessage(dto, request, response);

        ArgumentCaptor<MessageAccessContext> contextCaptor =
                ArgumentCaptor.forClass(MessageAccessContext.class);
        verify(createMessageUseCaseFacade).execute(eq(dto), contextCaptor.capture());
        MessageAccessContext context = contextCaptor.getValue();
        assertAll(
                () -> assertThat(context.legacyEnvironmentId()).isEqualTo("env-token"),
                () -> assertThat(context.externalIdentity()).isNull());
        verify(restPresenter).presentRestSuccess(
                List.of("Mensaje creado exitosamente"), request, response);
    }

    @Test
    void createMessage_includesExternalIdentityWhenAttributeIsPresent() {
        CreateMessageDTO dto = CreateMessageDTO.builder().environmentId("env-body").build();
        ExternalIdentity identity = new ExternalIdentity(
                "issuer", "subject", "user@example.com", Instant.MAX);
        when(request.getAttribute(ENVIRONMENT_ID_ATTRIBUTE)).thenReturn(null);
        when(request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE)).thenReturn(identity);

        controller.createMessage(dto, request, response);

        ArgumentCaptor<MessageAccessContext> contextCaptor =
                ArgumentCaptor.forClass(MessageAccessContext.class);
        verify(createMessageUseCaseFacade).execute(eq(dto), contextCaptor.capture());
        MessageAccessContext context = contextCaptor.getValue();
        assertAll(
                () -> assertThat(context.legacyEnvironmentId()).isNull(),
                () -> assertThat(context.externalIdentity()).isSameAs(identity));
        verify(restPresenter).presentRestSuccess(
                List.of("Mensaje creado exitosamente"), request, response);
    }

    @Test
    void createMessage_propagatesFacadeFailureWithoutPresentingSuccess() {
        CreateMessageDTO dto = CreateMessageDTO.builder().build();
        ForbiddenException failure = ForbiddenException.buildUserException("Permission denied");
        when(request.getAttribute(ENVIRONMENT_ID_ATTRIBUTE)).thenReturn("env-token");
        when(request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE)).thenReturn(null);
        doThrow(failure).when(createMessageUseCaseFacade)
                .execute(eq(dto), any(MessageAccessContext.class));

        assertThatThrownBy(() -> controller.createMessage(dto, request, response))
                .isSameAs(failure);
        verifyNoInteractions(restPresenter);
    }
}

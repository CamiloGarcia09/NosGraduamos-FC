package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.primaryports.facade.message.FindMessagesByEnvironmentUsecaseFacade;
import co.edu.uco.application.primaryports.facade.message.TranslateMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagesControllerImplTest {

    @Mock
    private FindMessagesByEnvironmentUsecaseFacade findMessagesByEnvironmentUsecaseFacade;
    @Mock
    private FindMessageByCodeAndEnvironmentUseCaseFacade findMessageByCodeAndEnvironmentUseCaseFacade;
    @Mock
    private TranslateMessageByCodeAndEnvironmentUseCaseFacade translateMessageByCodeAndEnvironmentUseCaseFacade;
    @Mock
    private PresenterPort<MessageDTO> restPresenter;
    @Mock
    private PresenterPort<TranslatedMessageDTO> translationPresenter;
    @Mock
    private PresenterPort<SimplePage<MessageDTO>> restPresenterPage;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private MessagesControllerImpl controller;

    @BeforeEach
    void setUp() {
        controller = new MessagesControllerImpl(
                findMessagesByEnvironmentUsecaseFacade,
                findMessageByCodeAndEnvironmentUseCaseFacade,
                translateMessageByCodeAndEnvironmentUseCaseFacade,
                restPresenter,
                translationPresenter,
                restPresenterPage);
    }

    @Test
    void findByEnvironmentAndMessage_buildsPageRequestAndPresentsResult() {
        when(request.getAttribute("environmentId")).thenReturn("env-1");
        SimplePage<MessageDTO> page = SimplePage.of(List.of(), 1, 10, 0, 0);
        when(findMessagesByEnvironmentUsecaseFacade.execute(
                org.mockito.ArgumentMatchers.eq("env-1"), org.mockito.ArgumentMatchers.any(PageRequestDTO.class)))
                .thenReturn(page);

        controller.findByEnvironmentAndMessage("1", "10", "asc", "code", request, response);

        ArgumentCaptor<PageRequestDTO> captor = ArgumentCaptor.forClass(PageRequestDTO.class);
        verify(findMessagesByEnvironmentUsecaseFacade).execute(org.mockito.ArgumentMatchers.eq("env-1"), captor.capture());
        PageRequestDTO captured = captor.getValue();
        assertThat(captured.getPage()).isEqualTo("1");
        assertThat(captured.getSize()).isEqualTo("10");
        assertThat(captured.getSort()).isEqualTo("asc");
        assertThat(captured.getColumnSort()).isEqualTo("code");
        verify(restPresenterPage).presentRestSuccess(List.of(page), request, response);
    }

    @Test
    void findByCodeMessageAndEnvironment_presentsSingleMessage() {
        when(request.getAttribute("environmentId")).thenReturn("env-1");
        MessageDTO dto = MessageDTO.create("CODE", "Title", "Content", "TYPE", "CAT", "APP", "FUNC");
        when(findMessageByCodeAndEnvironmentUseCaseFacade.execute("CODE", "env-1")).thenReturn(dto);

        controller.findByCodeMessageAndEnvironment("CODE", request, response);

        verify(restPresenter).presentRestSuccess(List.of(dto), request, response);
    }

    @Test
    void translateByCodeMessageAndEnvironment_presentsTranslatedMessage() {
        when(request.getAttribute("environmentId")).thenReturn("env-1");
        TranslatedMessageDTO dto = TranslatedMessageDTO.create("CODE", "es", "en", "T", "C", "TT", "TC",
                "TYPE", "CAT", "APP", "FUNC", "provider", "model", 10);
        when(translateMessageByCodeAndEnvironmentUseCaseFacade.execute("CODE", "env-1", "es", "en"))
                .thenReturn(dto);

        controller.translateByCodeMessageAndEnvironment("CODE", "es", "en", request, response);

        verify(translationPresenter).presentRestSuccess(List.of(dto), request, response);
    }
}
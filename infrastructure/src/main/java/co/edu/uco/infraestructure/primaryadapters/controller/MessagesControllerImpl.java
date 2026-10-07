package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.message.TranslateMessageDTO;
import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.primaryports.facade.message.FindMessagesByEnvironmentUsecaseFacade;
import co.edu.uco.application.primaryports.facade.message.TranslateMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import co.edu.uco.infraestructure.primaryadapters.MessagesController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.ENVIRONMENT_ID_ATTRIBUTE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;

@RestController
final class MessagesControllerImpl implements MessagesController {

        private final FindMessagesByEnvironmentUsecaseFacade findMessagesByEnvironmentUsecaseFacade;
        private final FindMessageByCodeAndEnvironmentUseCaseFacade findMessageByCodeAndEnvironmentUseCaseFacade;
        private final TranslateMessageByCodeAndEnvironmentUseCaseFacade translateMessageByCodeAndEnvironmentUseCaseFacade;
        private final PresenterPort<MessageDTO> restPresenter;
        private final PresenterPort<TranslatedMessageDTO> translationPresenter;
        private final PresenterPort<SimplePage<MessageDTO>> restPresenterPage;

        public MessagesControllerImpl(FindMessagesByEnvironmentUsecaseFacade findMessagesByEnvironmentUsecaseFacade,
                                      FindMessageByCodeAndEnvironmentUseCaseFacade findMessageByCodeAndEnvironmentUseCaseFacade,
                                      TranslateMessageByCodeAndEnvironmentUseCaseFacade translateMessageByCodeAndEnvironmentUseCaseFacade,
                                      PresenterPort<MessageDTO> restPresenter,
                                      PresenterPort<TranslatedMessageDTO> translationPresenter,
                                      PresenterPort<SimplePage<MessageDTO>> restPresenterPage) {
            this.findMessagesByEnvironmentUsecaseFacade = findMessagesByEnvironmentUsecaseFacade;
            this.findMessageByCodeAndEnvironmentUseCaseFacade = findMessageByCodeAndEnvironmentUseCaseFacade;
            this.translateMessageByCodeAndEnvironmentUseCaseFacade = translateMessageByCodeAndEnvironmentUseCaseFacade;
            this.restPresenter = restPresenter;
            this.translationPresenter = translationPresenter;
            this.restPresenterPage = restPresenterPage;
        }

        @Override
        public void findByEnvironmentAndMessage(
                        String page,
                        String size,
                        String sort,
                        String columnSort,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {

                var accessContext = accessContext(httpServletRequest);
                var pageRequestDTO = PageRequestDTO.builder()
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .columnSort(columnSort)
                        .build();
                
                var messageDTOSimplePage = findMessagesByEnvironmentUsecaseFacade.execute(accessContext, pageRequestDTO);
                restPresenterPage.presentRestSuccess(List.of(messageDTOSimplePage), httpServletRequest,
                                httpServletResponse);
        }

        @Override
        public void findByCodeMessageAndEnvironment(
                        String messageCode,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
                var accessContext = accessContext(httpServletRequest);
                var messageDTO = findMessageByCodeAndEnvironmentUseCaseFacade.execute(messageCode,
                                accessContext);
                restPresenter.presentRestSuccess(List.of(messageDTO), httpServletRequest, httpServletResponse);
        }

        @Override
        public void translateByCodeMessageAndEnvironment(
                        String messageCode,
                        TranslateMessageDTO translateMessageDTO,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
                var accessContext = accessContext(httpServletRequest);
                var translatedMessageDTO = translateMessageByCodeAndEnvironmentUseCaseFacade.execute(
                                messageCode,
                                accessContext,
                                translateMessageDTO.getSourceLanguage(),
                                translateMessageDTO.getTargetLanguage());
                translationPresenter.presentRestSuccess(List.of(translatedMessageDTO), httpServletRequest,
                                httpServletResponse);
        }

        private static MessageAccessContext accessContext(final HttpServletRequest request) {
                return new MessageAccessContext(
                        (String) request.getAttribute(ENVIRONMENT_ID_ATTRIBUTE),
                        (ExternalIdentity) request.getAttribute(EXTERNAL_IDENTITY_ATTRIBUTE));
        }
}

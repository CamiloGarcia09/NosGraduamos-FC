package co.edu.uco.application.usecase;

import co.edu.uco.application.common.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.common.mapper.entity.DataMapper;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.usecase.domain.MessageDomain;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.handling.HandlingFindMessageEnvironmentPort;
import co.edu.uco.application.usecase.validator.message.ListMessageValidator;
import co.edu.uco.application.usecase.validator.page.PageRequestRangeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.stereotype.Component;

@Component
public final class FindMessageByEnvironmentUseCase implements HandlingFindMessageEnvironmentPort {
    private final MessageCatalogStrategy messageCatalogStrategy;
    private final DataMapper<MessageData, MessageDomain,MessageDTO> entityMapper;
    private final ListMessageValidator listMessageValidator;
    private final PageRequestRangeValidator rangeValidator;
    private final LoggingPort log;

    public FindMessageByEnvironmentUseCase(MessageCatalogStrategy messageCatalogStrategy, DataMapper<MessageData, MessageDomain, MessageDTO> entityMapper,
                                           ListMessageValidator listMessageValidator, PageRequestRangeValidator rangeValidator,
                                           LoggingPortFactory loggerFactory) {
        this.messageCatalogStrategy = messageCatalogStrategy;
        this.entityMapper = entityMapper;
        this.listMessageValidator = listMessageValidator;
        this.rangeValidator = rangeValidator;
        this.log = loggerFactory.getLogger(FindMessageByEnvironmentUseCase.class);
    }
    @Override
    public SimplePage<MessageDTO> execute(String environment, SimplePageRequest pageRequest) {
        try {
            listMessageValidator.validate(pageRequest);
            var page = messageCatalogStrategy.getMessagesWithEnvironment(environment, pageRequest);
            rangeValidator.validate(pageRequest.getPage(), page.getTotalPages());
            var messages = page.getData().stream().map(entityMapper::mapperDTO).toList();
            return SimplePage.of(messages, page.getPage(), page.getSize(),page.getTotalItems(), page.getTotalPages());
        } catch (CrossWordsException exception) {
            throw exception;
        }
        catch (Exception exception){
            var errorMessage = messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_011.getCode());
            log.error(errorMessage);
            throw BusinessException.buildUserException(errorMessage);
        }
    }
}
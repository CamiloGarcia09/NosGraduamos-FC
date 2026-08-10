package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.application.dto.message.MessageDTO;
import co.edu.uco.core.application.mapper.entity.DataMapper;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.domains.MessageDomain;
import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPortFactory;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.usecase.handling.HandlingFindMessageEnvironmentPort;
import co.edu.uco.core.domain.validator.message.ListMessageValidator;
import co.edu.uco.core.domain.validator.page.PageRequestRangeValidator;
import co.edu.uco.utils.exception.BusinessException;
import co.edu.uco.utils.exception.CrossWordsException;
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
            var errorMessage = DetailMessageEnum.FUN_011.getContent();
            log.error(errorMessage);
            throw BusinessException.buildUserException(errorMessage);
        }
    }
}
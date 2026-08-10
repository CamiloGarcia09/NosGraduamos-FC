package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.application.dto.message.MessageDTO;
import co.edu.uco.core.application.mapper.entity.DataMapper;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.domains.MessageDomain;
import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPortFactory;
import co.edu.uco.core.domain.usecase.handling.HandlingFindMessageByCodeAndEnvironmentPort;
import co.edu.uco.core.domain.validator.message.FindMessageCodeValidator;
import co.edu.uco.utils.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public final class FindMessageByCodeAndEnvironmentUseCase implements HandlingFindMessageByCodeAndEnvironmentPort {
    private final MessageCatalogStrategy messageCatalogStrategy;
    private final FindMessageCodeValidator findMessageCodeValidator;
    private final DataMapper<MessageData, MessageDomain, MessageDTO> entityMapper;
    private final LoggingPort log;

    public FindMessageByCodeAndEnvironmentUseCase(MessageCatalogStrategy messageCatalogStrategy, FindMessageCodeValidator findMessageCodeValidator,
                                                  DataMapper<MessageData, MessageDomain, MessageDTO> entityMapper,
                                                  LoggingPortFactory loggerFactory) {
        this.messageCatalogStrategy = messageCatalogStrategy;
        this.findMessageCodeValidator = findMessageCodeValidator;
        this.entityMapper = entityMapper;
        this.log = loggerFactory.getLogger(FindMessageByCodeAndEnvironmentUseCase.class);
    }
    @Override
    public MessageDTO execute(String messageCode, String environmentId) {
        try {
            findMessageCodeValidator.validate(messageCode);
            var messageDataOptional = messageCatalogStrategy
                    .getMessageByCodeAndEnvironment(messageCode, environmentId);
            var messageData = messageDataOptional.orElseThrow(() -> {
                var errorMessage = String.format(DetailMessageEnum.FUN_012.getContent(), messageCode, environmentId);
                return BusinessException.buildUserException(errorMessage);
            });
            return entityMapper.mapperDTO(messageData);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            String errorMessage = String.format(DetailMessageEnum.FUN_012.getContent(), messageCode, environmentId);
            log.error(errorMessage, exception);
            throw BusinessException.buildUserException(errorMessage);
        }
    }
}
package co.edu.uco.application.usecase;

import co.edu.uco.application.common.catalog.strategy.MessageCatalogStrategy;
import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.common.mapper.entity.DataMapper;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.usecase.domain.MessageDomain;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.handling.HandlingFindMessageByCodeAndEnvironmentPort;
import co.edu.uco.application.usecase.validator.message.FindMessageCodeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
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
                var errorMessage = String.format(messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()), messageCode, environmentId);
                return BusinessException.buildUserException(errorMessage);
            });
            return entityMapper.mapperDTO(messageData);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            String errorMessage = String.format(messageCatalogStrategy.getSystemMessageContent(MessageCatalogCodeEnum.FUN_012.getCode()), messageCode, environmentId);
            log.error(errorMessage, exception);
            throw BusinessException.buildUserException(errorMessage);
        }
    }
}
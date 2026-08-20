package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.application.secondaryports.entity.StatusMessageData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.CreateMessageRepository;
import co.edu.uco.application.usecase.handling.HandlingCreateMessagePort;
import co.edu.uco.application.usecase.validator.message.CreateMessageCompositeValidator;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import org.springframework.stereotype.Component;

@Component
public final class CreateMessageUseCase implements HandlingCreateMessagePort {

    private final CreateMessageRepository createMessageRepository;
    private final CreateMessageCompositeValidator validator;
    private final LoggingPort log;

    public CreateMessageUseCase(
            CreateMessageRepository createMessageRepository,
            CreateMessageCompositeValidator validator,
            LoggingPortFactory loggerFactory) {
        this.createMessageRepository = createMessageRepository;
        this.validator = validator;
        this.log = loggerFactory.getLogger(CreateMessageUseCase.class);
    }

    @Override
    public void createMessage(CreateMessageDTO dto) {
        validator.validate(dto);

        try {
            var messageData = new MessageData(
                    UtilUUID.getNewUUID(),
                    dto.getCode(),
                    dto.getTitle(),
                    dto.getContent(),
                    MessageTypeData.build(dto.getTypeId()),
                    MessageCategoryData.build(dto.getCategoryId()),
                    dto.getApplication(),
                    new FunctionalityData(
                            UtilUUID.getStringToUUID(dto.getFunctionalityId()),
                            "",
                            ApplicationData.build(UtilUUID.getStringToUUID(dto.getApplicationId()), dto.getApplication()),
                            null,
                            null
                    )
            );
            messageData.setStatus(new StatusMessageData(UtilUUID.getNewUUID(), dto.getStatusId()));

            createMessageRepository.createMessage(
                    messageData,
                    dto.getEnvironmentId(),
                    dto.getMessageEnvironmentStateId()
            );

            log.info("Message created successfully with code: {}", dto.getCode());
        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating message in repository", ex);
            throw CrossWordsException.build("Error al crear el mensaje", ex);
        }
    }
}

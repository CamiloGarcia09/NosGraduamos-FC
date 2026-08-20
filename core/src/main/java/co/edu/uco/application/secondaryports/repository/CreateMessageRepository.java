package co.edu.uco.application.secondaryports.repository;

import co.edu.uco.application.secondaryports.entity.MessageData;

public interface CreateMessageRepository {
    void createMessage(MessageData message, String environmentId, String messageEnvironmentStateId);
}

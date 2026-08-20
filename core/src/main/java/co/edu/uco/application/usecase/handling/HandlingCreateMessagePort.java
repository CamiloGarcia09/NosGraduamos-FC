package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;

public interface HandlingCreateMessagePort {
    void createMessage(CreateMessageDTO messageDTO);
}

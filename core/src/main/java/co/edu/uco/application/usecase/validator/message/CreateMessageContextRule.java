package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;

public interface CreateMessageContextRule {
    void validate(CreateMessageDTO dto, String authenticatedEnvironmentId);
}

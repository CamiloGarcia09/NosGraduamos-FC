package co.edu.uco.core.domain.usecase.handling;

import co.edu.uco.core.application.dto.message.MessageDTO;

public interface HandlingFindMessageByCodeAndEnvironmentPort {
    MessageDTO execute(String messageCode, String environmentId);
}
package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;

public interface HandlingFindMessageByCodeAndEnvironmentPort {
    MessageDTO execute(String messageCode, String environmentId);
}
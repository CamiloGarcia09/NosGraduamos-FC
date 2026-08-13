package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;

public interface HandlingFindMessageEnvironmentPort {
    SimplePage<MessageDTO> execute(String environment, SimplePageRequest pageRequest);
}
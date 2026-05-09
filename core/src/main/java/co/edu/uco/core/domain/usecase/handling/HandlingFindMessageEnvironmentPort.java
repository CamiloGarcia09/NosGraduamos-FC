package co.edu.uco.core.domain.usecase.handling;

import co.edu.uco.core.application.dto.message.MessageDTO;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;

public interface HandlingFindMessageEnvironmentPort {
    SimplePage<MessageDTO> execute(String environment, SimplePageRequest pageRequest);
}
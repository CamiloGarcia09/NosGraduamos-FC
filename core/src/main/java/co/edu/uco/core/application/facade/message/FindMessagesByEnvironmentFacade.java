package co.edu.uco.core.application.facade.message;

import co.edu.uco.core.application.dto.message.MessageDTO;
import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.port.out.repository.SimplePage;

public interface FindMessagesByEnvironmentFacade {
    SimplePage<MessageDTO> execute(String environmentId, PageRequestDTO pageDTO);
}

package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.repository.SimplePage;

public interface FindMessagesByEnvironmentUsecaseFacade {
    SimplePage<MessageDTO> execute(String environmentId, PageRequestDTO pageDTO);
}

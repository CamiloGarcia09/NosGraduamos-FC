package co.edu.uco.application.primaryports.facade.message;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.application.usecase.domain.security.MessageAccessContext;

public interface FindMessagesByEnvironmentUsecaseFacade {
    SimplePage<MessageDTO> execute(MessageAccessContext context, PageRequestDTO pageDTO);
}

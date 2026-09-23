package co.edu.uco.application.primaryports.facade.context;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

import java.util.List;

public interface ActiveContextUseCaseFacade {

    List<AvailableContextDTO> findAvailableContexts(ExternalIdentity identity);

    ActiveContextDTO findActiveContext(ExternalIdentity identity);

    ActiveContextDTO selectActiveContext(SelectActiveContextDTO context, ExternalIdentity identity);
}

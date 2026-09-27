package co.edu.uco.application.primaryports.facade.functionality;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface CreateFunctionalityUseCaseFacade {
    void execute(CreateFunctionalityDTO createFunctionalityDTO, ExternalIdentity identity);
}
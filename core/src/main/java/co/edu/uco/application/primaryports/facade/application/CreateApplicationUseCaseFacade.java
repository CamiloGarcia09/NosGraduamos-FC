package co.edu.uco.application.primaryports.facade.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface CreateApplicationUseCaseFacade {
    void execute(CreateApplicationDTO createApplicationDTO, ExternalIdentity identity);
}
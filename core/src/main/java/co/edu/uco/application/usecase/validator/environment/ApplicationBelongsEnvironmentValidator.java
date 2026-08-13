package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getStringFromUUID;

@Component
public final class ApplicationBelongsEnvironmentValidator {
    private final EnvironmentRepository repository;
    private final CatalogPort catalogPort;
    public ApplicationBelongsEnvironmentValidator(EnvironmentRepository repository, CatalogPort catalogPort) {
        this.repository = repository;
        this.catalogPort = catalogPort;
    }
    public void validate(UUID environmentId, UUID applicationId) throws BusinessRuleException {
        Optional<EnvironmentData> environment = repository.findById(getStringFromUUID(environmentId));
        environment.ifPresent(environmentData -> {
            if (!environmentData.getApplication().getId().equals(applicationId)) {
                throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_036"));
            }
        });
    }
}
package co.edu.uco.core.domain.validator.environment;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.utils.helper.UtilUUID.getStringFromUUID;

@Component
public final class ApplicationBelongsEnvironmentValidator {
    private final EnvironmentRepository repository;
    private final InMemoryCatalog inMemoryCatalog;
    public ApplicationBelongsEnvironmentValidator(EnvironmentRepository repository, InMemoryCatalog inMemoryCatalog) {
        this.repository = repository;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    public void validate(UUID environmentId, UUID applicationId) throws BusinessRuleException {
        Optional<EnvironmentData> environment = repository.findById(getStringFromUUID(environmentId));
        environment.ifPresent(environmentData -> {
            if (!environmentData.getApplication().getId().equals(applicationId)) {
                throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_036.getKey()));
            }
        });
    }
}
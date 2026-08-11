package co.edu.uco.core.domain.validator.environment;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.application.dto.token.CreateTokenDTO;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public final class EnvironmentExistValidator implements Validator<CreateTokenDTO> {
    private final EnvironmentRepository repository;
    private final InMemoryCatalog inMemoryCatalog;
    public EnvironmentExistValidator(EnvironmentRepository repository, InMemoryCatalog inMemoryCatalog) {
        this.repository = repository;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(CreateTokenDTO data) throws BusinessRuleException {
        if (repository.findById(data.getEnvironmentId()).isEmpty()) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_035.getKey()));
        }
    }
}
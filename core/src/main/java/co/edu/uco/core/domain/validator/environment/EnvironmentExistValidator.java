package co.edu.uco.core.domain.validator.environment;

import co.edu.uco.core.application.dto.token.CreateTokenDTO;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public final class EnvironmentExistValidator implements Validator<CreateTokenDTO> {
    private final EnvironmentRepository repository;
    private final CatalogPort catalogPort;
    public EnvironmentExistValidator(EnvironmentRepository repository, CatalogPort catalogPort) {
        this.repository = repository;
        this.catalogPort = catalogPort;
    }
    @Override
    public void validate(CreateTokenDTO data) throws BusinessRuleException {
        if (repository.findById(data.getEnvironmentId()).isEmpty()) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("FUN_035"));
        }
    }
}
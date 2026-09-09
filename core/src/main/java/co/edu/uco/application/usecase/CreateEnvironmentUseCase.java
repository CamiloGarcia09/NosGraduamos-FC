package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.handling.HandlingCreateEnvironmentPort;
import co.edu.uco.application.usecase.validator.environment.CreateEnvironmentCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import org.springframework.stereotype.Component;

@Component
public final class CreateEnvironmentUseCase implements HandlingCreateEnvironmentPort {

    private final EnvironmentRepository environmentRepository;
    private final CreateEnvironmentCompositeValidator validator;
    private final LoggingPort log;

    public CreateEnvironmentUseCase(EnvironmentRepository environmentRepository,
                                    CreateEnvironmentCompositeValidator validator,
                                    LoggingPortFactory loggerFactory) {
        this.environmentRepository = environmentRepository;
        this.validator = validator;
        this.log = loggerFactory.getLogger(CreateEnvironmentUseCase.class);
    }

    @Override
    public void createEnvironment(CreateEnvironmentDTO dto) {
        validator.validate(dto);

        try {
            var application = ApplicationData.build(UtilUUID.getStringToUUID(dto.getApplicationId()), "");
            var environment = new EnvironmentData(UtilUUID.getNewUUID(), dto.getName(), application);
            environmentRepository.create(environment, dto.getTypeId(), dto.getStateId());
            log.info("Environment created successfully with name: {}", dto.getName());
        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating environment in repository", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al crear el entorno", ex, ExceptionLocation.APPLICATION);
        }
    }
}
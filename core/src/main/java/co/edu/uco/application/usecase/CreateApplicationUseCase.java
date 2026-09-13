package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.handling.HandlingCreateApplicationPort;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;

@Component
public final class CreateApplicationUseCase implements HandlingCreateApplicationPort {

    private final ApplicationRepository applicationRepository;
    private final CreateApplicationCompositeValidator validator;
    private final LoggingPort log;

    public CreateApplicationUseCase(ApplicationRepository applicationRepository,
                                    CreateApplicationCompositeValidator validator,
                                    LoggingPortFactory loggerFactory) {
        this.applicationRepository = applicationRepository;
        this.validator = validator;
        this.log = loggerFactory.getLogger(CreateApplicationUseCase.class);
    }

    @Override
    public void createApplication(CreateApplicationDTO dto) {
        validator.validate(dto);

        try {
            var application = new ApplicationData(UtilUUID.getNewUUID(), dto.getName());
            applicationRepository.create(
                    application,
                    dto.getLanguageId(),
                    parseDate(dto.getStartDate()),
                    parseDate(dto.getEndDate()),
                    dto.getStateId()
            );
            log.info("Application created successfully with name: {}", dto.getName());
        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating application in repository", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al crear la aplicación", ex, ExceptionLocation.APPLICATION);
        }
    }
}
package co.edu.uco.application.usecase.validator.functionality;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.CompositeValidatorSupport;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public final class CreateFunctionalityCompositeValidator extends CompositeValidatorSupport {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre de la funcionalidad es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre de la funcionalidad no puede superar los 50 caracteres.";
    private static final String MESSAGE_APPLICATION_REQUIRED = "El id de la aplicación es requerido.";
    private static final String MESSAGE_APPLICATION_NOT_EXISTS = "La aplicación a la que se asocia la funcionalidad no existe.";
    private static final String MESSAGE_START_DATE_REQUIRED = "La fecha de inicio de la funcionalidad es requerida.";
    private static final String MESSAGE_END_DATE_REQUIRED = "La fecha de fin de la funcionalidad es requerida.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado de la funcionalidad es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado de la funcionalidad no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe una funcionalidad con el mismo nombre para la aplicación.";

    private final FunctionalityRepository functionalityRepository;
    private final UUIDValidator uuidValidator;
    private final DateValidValidator dateValidValidator;

    public CreateFunctionalityCompositeValidator(CatalogPort catalogPort,
                                                 RecordExistsCatalogPort recordExistsCatalogPort,
                                                 ApplicationRepository applicationRepository,
                                                 FunctionalityRepository functionalityRepository,
                                                 UUIDValidator uuidValidator,
                                                 DateValidValidator dateValidValidator) {
        super(catalogPort, recordExistsCatalogPort, applicationRepository);
        this.functionalityRepository = functionalityRepository;
        this.uuidValidator = uuidValidator;
        this.dateValidValidator = dateValidValidator;
    }

    public void validate(CreateFunctionalityDTO dto) {
        validateNotNull(dto);

        validateRequiredName(dto.getName(), MESSAGE_NAME_REQUIRED, MESSAGE_NAME_MAX_LENGTH);

        validateRequiredAndExistingApplicationId(dto.getApplicationId(), uuidValidator,
                MESSAGE_APPLICATION_REQUIRED, MESSAGE_APPLICATION_NOT_EXISTS);

        validateDateRange(dto.getStartDate(), dto.getEndDate(),
                MESSAGE_START_DATE_REQUIRED, MESSAGE_END_DATE_REQUIRED, dateValidValidator);

        validateCatalogReference(dto.getStateId(), ReferenceCatalog.FUNCTIONALITY_STATE,
                MESSAGE_STATE_REQUIRED, MESSAGE_STATE_NOT_EXISTS);

        if (functionalityRepository.existsByNameAndApplicationId(dto.getName(), dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
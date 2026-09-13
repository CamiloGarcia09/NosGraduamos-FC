package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.CompositeValidatorSupport;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public final class CreateApplicationCompositeValidator extends CompositeValidatorSupport {

    private static final String MESSAGE_NAME_REQUIRED = "El nombre de la aplicación es requerido.";
    private static final String MESSAGE_NAME_MAX_LENGTH = "El nombre de la aplicación no puede superar los 50 caracteres.";
    private static final String MESSAGE_LANGUAGE_REQUIRED = "El idioma de la aplicación es requerido.";
    private static final String MESSAGE_LANGUAGE_NOT_EXISTS = "El idioma de la aplicación no existe.";
    private static final String MESSAGE_START_DATE_REQUIRED = "La fecha de inicio de la aplicación es requerida.";
    private static final String MESSAGE_END_DATE_REQUIRED = "La fecha de fin de la aplicación es requerida.";
    private static final String MESSAGE_STATE_REQUIRED = "El estado de la aplicación es requerido.";
    private static final String MESSAGE_STATE_NOT_EXISTS = "El estado de la aplicación no existe.";
    private static final String MESSAGE_NAME_DUPLICATED = "Ya existe una aplicación con el nombre proporcionado.";

    private final DateValidValidator dateValidValidator;

    public CreateApplicationCompositeValidator(CatalogPort catalogPort,
                                               RecordExistsCatalogPort recordExistsCatalogPort,
                                               ApplicationRepository applicationRepository,
                                               DateValidValidator dateValidValidator) {
        super(catalogPort, recordExistsCatalogPort, applicationRepository);
        this.dateValidValidator = dateValidValidator;
    }

    public void validate(CreateApplicationDTO dto) {
        validateNotNull(dto);

        validateRequiredName(dto.getName(), MESSAGE_NAME_REQUIRED, MESSAGE_NAME_MAX_LENGTH);

        validateCatalogReference(dto.getLanguageId(), ReferenceCatalog.LANGUAGE_BASE,
                MESSAGE_LANGUAGE_REQUIRED, MESSAGE_LANGUAGE_NOT_EXISTS);

        validateDateRange(dto.getStartDate(), dto.getEndDate(),
                MESSAGE_START_DATE_REQUIRED, MESSAGE_END_DATE_REQUIRED, dateValidValidator);

        validateCatalogReference(dto.getStateId(), ReferenceCatalog.APPLICATION_STATE,
                MESSAGE_STATE_REQUIRED, MESSAGE_STATE_NOT_EXISTS);

        if (applicationRepository.findByName(dto.getName()).isPresent()) {
            throw BusinessRuleException.buildUserException(MESSAGE_NAME_DUPLICATED);
        }
    }
}
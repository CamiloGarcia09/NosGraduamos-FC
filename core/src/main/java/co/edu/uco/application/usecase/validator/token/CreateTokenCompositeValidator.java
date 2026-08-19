package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.usecase.validator.environment.ApplicationBelongsEnvironmentValidator;
import co.edu.uco.application.usecase.validator.environment.EnvironmentExistValidator;
import co.edu.uco.application.usecase.validator.impl.ExpirationDateValidator;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

@Component
public final class CreateTokenCompositeValidator {

    private final ApplicationBelongsEnvironmentValidator applicationBelongsEnvironmentValidator;
    private final EnvironmentExistValidator environmentExistValidator;
    private final ExpirationDateValidator expirationDateValidator;
    private final DateValidValidator dateValidValidator;
    private final UUIDValidator uuidValidator;

    public CreateTokenCompositeValidator(ApplicationBelongsEnvironmentValidator applicationBelongsEnvironmentValidator,
                                         EnvironmentExistValidator environmentExistValidator,
                                         ExpirationDateValidator expirationDateValidator,
                                         DateValidValidator dateValidValidator,
                                         UUIDValidator uuidValidator) {
        this.applicationBelongsEnvironmentValidator = applicationBelongsEnvironmentValidator;
        this.environmentExistValidator = environmentExistValidator;
        this.expirationDateValidator = expirationDateValidator;
        this.dateValidValidator = dateValidValidator;
        this.uuidValidator = uuidValidator;
    }

    public void validate(CreateTokenDTO createTokenDTO, String applicationId) {

        uuidValidator.validate(applicationId);
        uuidValidator.validate(createTokenDTO.getEnvironmentId());
        dateValidValidator.validate(createTokenDTO.getExpirationDate());
        expirationDateValidator.validate(parseDate(createTokenDTO.getExpirationDate()));
        environmentExistValidator.validate(createTokenDTO);
        applicationBelongsEnvironmentValidator.validate(getUUIDFromString(createTokenDTO.getEnvironmentId()),
                getUUIDFromString(applicationId));
    }
}
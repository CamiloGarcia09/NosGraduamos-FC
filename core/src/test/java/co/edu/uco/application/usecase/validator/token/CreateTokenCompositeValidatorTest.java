package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.usecase.validator.environment.ApplicationBelongsEnvironmentValidator;
import co.edu.uco.application.usecase.validator.environment.EnvironmentExistValidator;
import co.edu.uco.application.usecase.validator.impl.ExpirationDateValidator;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTokenCompositeValidatorTest {

    @Mock
    private ApplicationBelongsEnvironmentValidator applicationBelongsEnvironmentValidator;
    @Mock
    private EnvironmentExistValidator environmentExistValidator;
    @Mock
    private ExpirationDateValidator expirationDateValidator;
    @Mock
    private DateValidValidator dateValidValidator;
    @Mock
    private UUIDValidator uuidValidator;

    private CreateTokenCompositeValidator validator;
    private final String applicationId = "123e4567-e89b-12d3-a456-426614174000";
    private final String environmentId = "123e4567-e89b-12d3-a456-426614174100";

    @BeforeEach
    void setUp() {
        validator = new CreateTokenCompositeValidator(
                applicationBelongsEnvironmentValidator,
                environmentExistValidator,
                expirationDateValidator,
                dateValidValidator,
                uuidValidator);
    }

    private CreateTokenDTO validDto() {
        CreateTokenDTO dto = new CreateTokenDTO();
        dto.setEnvironmentId(environmentId);
        dto.setExpirationDate(LocalDateTime.now().plusDays(1).toString());
        return dto;
    }

    @Test
    void validate_runsAllValidators_whenInputIsValid() {
        CreateTokenDTO dto = validDto();
        assertThatCode(() -> validator.validate(dto, applicationId)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotProceed_whenApplicationUuidIsInvalid() {
        org.mockito.Mockito.doThrow(BusinessRuleException.buildUserException("invalid app"))
                .when(uuidValidator).validate(anyString());
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto, "not-a-uuid"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid app"));
    }

@Test
    void validate_doesNotProceed_whenEnvironmentUuidIsInvalid() {
        org.mockito.Mockito.doNothing().when(uuidValidator).validate(anyString());
        org.mockito.Mockito.doThrow(BusinessRuleException.buildUserException("invalid env"))
                .when(uuidValidator).validate(environmentId);
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto, applicationId))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid env"));
    }

    @Test
    void validate_doesNotProceed_whenDateIsInvalid() {
        org.mockito.Mockito.doThrow(BusinessRuleException.buildUserException("invalid date"))
                .when(dateValidValidator).validate(anyString());
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto, applicationId))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid date"));
    }

    @Test
    void validate_doesNotProceed_whenEnvironmentDoesNotExist() {
        org.mockito.Mockito.doThrow(BusinessRuleException.buildUserException("env not found"))
                .when(environmentExistValidator).validate(any(CreateTokenDTO.class));
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto, applicationId))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("env not found"));
    }

    @Test
    void validate_doesNotProceed_whenApplicationDoesNotBelongToEnvironment() {
        org.mockito.Mockito.doThrow(BusinessRuleException.buildUserException("not belongs"))
                .when(applicationBelongsEnvironmentValidator).validate(any(UUID.class), any(UUID.class));
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto, applicationId))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("not belongs"));
    }
}

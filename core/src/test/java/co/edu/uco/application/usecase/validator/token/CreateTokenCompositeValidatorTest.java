package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTokenCompositeValidatorTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614174100";
    private static final String OTHER_APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174200";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private Clock clock;

    private CreateTokenCompositeValidator validator;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        validator = new CreateTokenCompositeValidator(catalogPort, environmentRepository, clock);
    }

    private CreateTokenDTO validDto() {
        CreateTokenDTO dto = new CreateTokenDTO();
        dto.setEnvironmentId(ENVIRONMENT_ID);
        dto.setExpirationDate("2026-01-01T00:00:00");
        return dto;
    }

    private void environmentBelongs(UUID applicationId) {
        when(environmentRepository.findById(ENVIRONMENT_ID))
                .thenReturn(Optional.of(new EnvironmentData(UUID.fromString(ENVIRONMENT_ID), "Production",
                        new ApplicationData(applicationId, "Message App"))));
    }

    @Test
    void validate_acceptsValidContext() {
        environmentBelongs(UUID.fromString(APPLICATION_ID));

        assertThatCode(() -> validator.validate(new TokenValidationContext(validDto(), APPLICATION_ID)))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotProceed_whenApplicationUuidIsInvalid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn("invalid app");
        CreateTokenDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(dto, "not-a-uuid")))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid app"));
    }

    @Test
    void validate_doesNotProceed_whenEnvironmentUuidIsInvalid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn("invalid env");
        CreateTokenDTO dto = validDto();
        dto.setEnvironmentId("not-a-uuid");

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(dto, APPLICATION_ID)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid env"));
    }

    @Test
    void validate_doesNotProceed_whenDateIsInvalid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_039.getCode())).thenReturn("invalid date");
        CreateTokenDTO dto = validDto();
        dto.setExpirationDate("not-a-date");

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(dto, APPLICATION_ID)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("invalid date"));
    }

    @Test
    void validate_doesNotProceed_whenExpirationHasPassed() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_037.getCode())).thenReturn("expiration passed");
        CreateTokenDTO dto = validDto();
        dto.setExpirationDate("2020-01-01T00:00:00");

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(dto, APPLICATION_ID)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("expiration passed"));
    }

    @Test
    void validate_doesNotProceed_whenEnvironmentDoesNotExist() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_035.getCode())).thenReturn("env not found");
        when(environmentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(validDto(), APPLICATION_ID)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("env not found"));
    }

    @Test
    void validate_doesNotProceed_whenApplicationDoesNotBelongToEnvironment() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_036.getCode())).thenReturn("not belongs");
        environmentBelongs(UUID.fromString(OTHER_APPLICATION_ID));

        assertThatThrownBy(() -> validator.validate(new TokenValidationContext(validDto(), APPLICATION_ID)))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage()).isEqualTo("not belongs"));
    }
}
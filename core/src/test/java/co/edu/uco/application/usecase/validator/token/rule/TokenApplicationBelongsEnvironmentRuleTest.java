package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenApplicationBelongsEnvironmentRuleTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String OTHER_APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174200";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614174100";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private TokenApplicationBelongsEnvironmentRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new TokenApplicationBelongsEnvironmentRule(catalogPort, environmentRepository);
    }

    private TokenValidationContext context() {
        CreateTokenDTO dto = new CreateTokenDTO("2026-01-01T00:00:00", ENVIRONMENT_ID);
        return new TokenValidationContext(dto, APPLICATION_ID);
    }

    private EnvironmentData environmentBelongingTo(UUID applicationId) {
        return new EnvironmentData(UUID.fromString(ENVIRONMENT_ID), "Production",
                new ApplicationData(applicationId, "Message App"));
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentDoesNotExist() {
        when(environmentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> rule.validate(context()));
    }

    @Test
    void validate_doesNotThrow_whenApplicationBelongsToEnvironment() {
        when(environmentRepository.findById(anyString()))
                .thenReturn(Optional.of(environmentBelongingTo(UUID.fromString(APPLICATION_ID))));

        assertDoesNotThrow(() -> rule.validate(context()));
    }

    @Test
    void validate_throwsBusinessRuleException_whenApplicationDoesNotBelongToEnvironment() {
        when(environmentRepository.findById(anyString()))
                .thenReturn(Optional.of(environmentBelongingTo(UUID.fromString(OTHER_APPLICATION_ID))));

        assertThrows(BusinessRuleException.class, () -> rule.validate(context()));
    }
}
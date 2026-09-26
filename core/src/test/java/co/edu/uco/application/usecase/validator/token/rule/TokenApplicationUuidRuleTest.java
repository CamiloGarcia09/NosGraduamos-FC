package co.edu.uco.application.usecase.validator.token.rule;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.token.TokenValidationContext;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TokenApplicationUuidRuleTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614174100";

    @Mock
    private CatalogPort catalogPort;

    private TokenApplicationUuidRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new TokenApplicationUuidRule(catalogPort);
    }

    private TokenValidationContext context(String applicationId) {
        CreateTokenDTO dto = new CreateTokenDTO("2026-01-01T00:00:00", ENVIRONMENT_ID);
        return new TokenValidationContext(dto, applicationId);
    }

    @Test
    void validate_doesNotThrow_whenApplicationIdIsValidUuid() {
        assertDoesNotThrow(() -> rule.validate(context(APPLICATION_ID)));
    }

    @Test
    void validate_throwsBusinessRuleException_whenApplicationIdIsNotAUuid() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(context("not-a-uuid")));
    }

    @Test
    void validate_throwsBusinessRuleException_whenContextIsNull() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(null));
    }
}
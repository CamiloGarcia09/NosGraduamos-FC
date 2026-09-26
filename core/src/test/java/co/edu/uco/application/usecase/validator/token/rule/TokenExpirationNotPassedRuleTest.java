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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TokenExpirationNotPassedRuleTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614174100";

    @Mock
    private CatalogPort catalogPort;

    private TokenExpirationNotPassedRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        rule = new TokenExpirationNotPassedRule(catalogPort, clock);
    }

    private TokenValidationContext context(String expirationDate) {
        CreateTokenDTO dto = new CreateTokenDTO(expirationDate, ENVIRONMENT_ID);
        return new TokenValidationContext(dto, APPLICATION_ID);
    }

    @Test
    void validate_doesNotThrow_whenExpirationIsInTheFuture() {
        assertDoesNotThrow(() -> rule.validate(context("2026-01-01T00:00:00")));
    }

    @Test
    void validate_throwsBusinessRuleException_whenExpirationHasPassed() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(context("2020-01-01T00:00:00")));
    }

    @Test
    void validate_throwsBusinessRuleException_whenContextIsNull() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(null));
    }
}
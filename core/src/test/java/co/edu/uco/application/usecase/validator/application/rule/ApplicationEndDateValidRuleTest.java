package co.edu.uco.application.usecase.validator.application.rule;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
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
class ApplicationEndDateValidRuleTest {

    @Mock
    private CatalogPort catalogPort;

    private ApplicationEndDateValidRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new ApplicationEndDateValidRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenEndDateIsValid() {
        CreateApplicationDTO dto = CreateApplicationDTO.builder().endDate("2025-12-31T23:59:59").build();

        assertDoesNotThrow(() -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenEndDateIsInvalid() {
        CreateApplicationDTO dto = CreateApplicationDTO.builder().endDate("not-a-date").build();

        assertThrows(BusinessRuleException.class, () -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenDtoIsNull() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(null));
    }
}
package co.edu.uco.application.usecase.validator.functionality.rule;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
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
class FunctionalityDateRangeOrderRuleTest {

    @Mock
    private CatalogPort catalogPort;

    private FunctionalityDateRangeOrderRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new FunctionalityDateRangeOrderRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenStartDateIsBeforeEndDate() {
        CreateFunctionalityDTO dto = CreateFunctionalityDTO.builder()
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .build();

        assertDoesNotThrow(() -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenStartDateIsAfterEndDate() {
        CreateFunctionalityDTO dto = CreateFunctionalityDTO.builder()
                .startDate("2026-01-01T00:00:00")
                .endDate("2025-01-01T00:00:00")
                .build();

        assertThrows(BusinessRuleException.class, () -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenDtoIsNull() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(null));
    }
}
package co.edu.uco.application.usecase.validator.application.rule;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationLanguageExistsRuleTest {

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;

    private ApplicationLanguageExistsRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new ApplicationLanguageExistsRule(catalogPort, recordExistsCatalogPort);
    }

    @Test
    void validate_doesNotThrow_whenLanguageExists() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateApplicationDTO dto = CreateApplicationDTO.builder().languageId("lang-1").build();

        assertDoesNotThrow(() -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenLanguageDoesNotExist() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(false);
        CreateApplicationDTO dto = CreateApplicationDTO.builder().languageId("unknown").build();

        assertThrows(BusinessRuleException.class, () -> rule.validate(dto));
    }

    @Test
    void validate_throwsBusinessRuleException_whenDtoIsNull() {
        assertThrows(BusinessRuleException.class, () -> rule.validate(null));
    }
}
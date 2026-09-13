package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateApplicationCompositeValidatorTest {

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;
    @Mock
    private ApplicationRepository applicationRepository;

    private CreateApplicationCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateApplicationCompositeValidator(
                catalogPort, recordExistsCatalogPort, applicationRepository, new DateValidValidator(catalogPort));
    }

    private CreateApplicationDTO validDto() {
        return CreateApplicationDTO.builder()
                .name("Message App")
                .languageId("lang-1")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state-1")
                .build();
    }

    @Test
    void validate_acceptsValidDto() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(applicationRepository.findByName("Message App")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validate(validDto()));
    }

    @Test
    void validate_throwsBusinessRule_whenDtoIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())).thenReturn("Datos no validos");

        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Datos no validos"));
    }

    @Test
    void validate_throwsBusinessRule_whenNameIsEmpty() {
        assertThatThrownBy(() -> validator.validate(new CreateApplicationDTO()))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameExceedsMaxLength() {
        CreateApplicationDTO dto = validDto();
        dto.setName("a".repeat(51));

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre de la aplicación no puede superar los 50 caracteres."));
    }

    @Test
    void validate_throwsBusinessRule_whenLanguageIdIsEmpty() {
        CreateApplicationDTO dto = validDto();
        dto.setLanguageId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El idioma de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenLanguageDoesNotExist() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(false);
        CreateApplicationDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El idioma de la aplicación no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenStartDateIsEmpty() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateApplicationDTO dto = validDto();
        dto.setStartDate("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de inicio de la aplicación es requerida."));
    }

    @Test
    void validate_throwsBusinessRule_whenStartDateIsAfterEndDate() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateApplicationDTO dto = validDto();
        dto.setStartDate("2026-12-31T23:59:59");
        dto.setEndDate("2025-01-01T00:00:00");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de inicio no puede ser posterior a la fecha de fin."));
    }

    @Test
    void validate_throwsBusinessRule_whenStateIdIsEmpty() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateApplicationDTO dto = validDto();
        dto.setStateId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenStateDoesNotExist() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true, false);
        CreateApplicationDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la aplicación no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameAlreadyExists() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(applicationRepository.findByName("Message App"))
                .thenReturn(Optional.of(new ApplicationData()));
        CreateApplicationDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Ya existe una aplicación con el nombre proporcionado."));
    }
}
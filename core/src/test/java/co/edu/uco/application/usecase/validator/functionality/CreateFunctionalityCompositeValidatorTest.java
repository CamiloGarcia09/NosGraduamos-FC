package co.edu.uco.application.usecase.validator.functionality;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFunctionalityCompositeValidatorTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private FunctionalityRepository functionalityRepository;

    private CreateFunctionalityCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateFunctionalityCompositeValidator(catalogPort, recordExistsCatalogPort,
                applicationRepository, functionalityRepository);
    }

    private CreateFunctionalityDTO validDto() {
        return CreateFunctionalityDTO.builder()
                .name("Search messages")
                .applicationId(APP_UUID)
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state-1")
                .build();
    }

    @Test
    void validate_acceptsValidDto() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(functionalityRepository.existsByNameAndApplicationId("Search messages", APP_UUID)).thenReturn(false);

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
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_163.getCode()))
                .thenReturn("El nombre de la funcionalidad es requerido.");

        assertThatThrownBy(() -> validator.validate(new CreateFunctionalityDTO()))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre de la funcionalidad es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameExceedsMaxLength() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_164.getCode()))
                .thenReturn("El nombre de la funcionalidad no puede superar los 50 caracteres.");
        CreateFunctionalityDTO dto = validDto();
        dto.setName("a".repeat(51));

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre de la funcionalidad no puede superar los 50 caracteres."));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationIdIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn("El id de la aplicación es requerido.");
        CreateFunctionalityDTO dto = validDto();
        dto.setApplicationId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationDoesNotExist() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_165.getCode()))
                .thenReturn("La aplicación a la que se asocia la funcionalidad no existe.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(false);
        CreateFunctionalityDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La aplicación a la que se asocia la funcionalidad no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenStartDateIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_166.getCode()))
                .thenReturn("La fecha de inicio de la funcionalidad es requerida.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        CreateFunctionalityDTO dto = validDto();
        dto.setStartDate("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de inicio de la funcionalidad es requerida."));
    }

    @Test
    void validate_throwsBusinessRule_whenEndDateIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_167.getCode()))
                .thenReturn("La fecha de fin de la funcionalidad es requerida.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        CreateFunctionalityDTO dto = validDto();
        dto.setEndDate("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de fin de la funcionalidad es requerida."));
    }

    @Test
    void validate_throwsBusinessRule_whenStartDateIsAfterEndDate() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_173.getCode()))
                .thenReturn("La fecha de inicio no puede ser posterior a la fecha de fin.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        CreateFunctionalityDTO dto = validDto();
        dto.setStartDate("2026-12-31T23:59:59");
        dto.setEndDate("2025-01-01T00:00:00");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La fecha de inicio no puede ser posterior a la fecha de fin."));
    }

    @Test
    void validate_throwsBusinessRule_whenStateIdIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_168.getCode()))
                .thenReturn("El estado de la funcionalidad es requerido.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        CreateFunctionalityDTO dto = validDto();
        dto.setStateId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la funcionalidad es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenStateDoesNotExist() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_169.getCode()))
                .thenReturn("El estado de la funcionalidad no existe.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(false);
        CreateFunctionalityDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la funcionalidad no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameAlreadyExistsForApplication() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_170.getCode()))
                .thenReturn("Ya existe una funcionalidad con el mismo nombre para la aplicación.");
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(functionalityRepository.existsByNameAndApplicationId("Search messages", APP_UUID)).thenReturn(true);
        CreateFunctionalityDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Ya existe una funcionalidad con el mismo nombre para la aplicación."));
    }
}
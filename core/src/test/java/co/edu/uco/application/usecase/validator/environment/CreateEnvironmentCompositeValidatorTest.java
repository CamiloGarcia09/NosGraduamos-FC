package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
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
class CreateEnvironmentCompositeValidatorTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private EnvironmentRepository environmentRepository;

    private CreateEnvironmentCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateEnvironmentCompositeValidator(catalogPort, recordExistsCatalogPort,
                applicationRepository, environmentRepository, new UUIDValidator(catalogPort));
    }

    private CreateEnvironmentDTO validDto() {
        return CreateEnvironmentDTO.builder()
                .name("Production")
                .applicationId(APP_UUID)
                .typeId("type-1")
                .stateId("state-1")
                .build();
    }

    @Test
    void validate_acceptsValidDto() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(environmentRepository.existsByNameAndApplicationId("Production", APP_UUID)).thenReturn(false);

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
        assertThatThrownBy(() -> validator.validate(new CreateEnvironmentDTO()))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre del entorno es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameExceedsMaxLength() {
        CreateEnvironmentDTO dto = validDto();
        dto.setName("a".repeat(51));

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El nombre del entorno no puede superar los 50 caracteres."));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationIdIsEmpty() {
        CreateEnvironmentDTO dto = validDto();
        dto.setApplicationId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationDoesNotExist() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(false);
        CreateEnvironmentDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("La aplicación a la que se asocia el entorno no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationIdIsDefaultUuid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn("El uuid no es valido");
        CreateEnvironmentDTO dto = validDto();
        dto.setApplicationId("00000000-0000-0000-0000-000000000000");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El uuid no es valido"));
    }

    @Test
    void validate_throwsBusinessRule_whenTypeIdIsEmpty() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        CreateEnvironmentDTO dto = validDto();
        dto.setTypeId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El tipo del entorno es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenTypeDoesNotExist() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(false);
        CreateEnvironmentDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El tipo de entorno no existe."));
    }

    @Test
    void validate_throwsBusinessRule_whenStateIdIsEmpty() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateEnvironmentDTO dto = validDto();
        dto.setStateId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado del entorno es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenNameAlreadyExistsForApplication() {
        when(applicationRepository.existsById(APP_UUID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(environmentRepository.existsByNameAndApplicationId("Production", APP_UUID)).thenReturn(true);
        CreateEnvironmentDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Ya existe un entorno con el mismo nombre para la aplicación."));
    }
}
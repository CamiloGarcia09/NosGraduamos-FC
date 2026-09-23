package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageCompositeValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    private CreateMessageCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateMessageCompositeValidator(catalogPort);
    }

    private CreateMessageDTO validDto() {
        return CreateMessageDTO.builder()
                .code("MSG-001")
                .title("A valid title")
                .content("A valid message content")
                .applicationId("app-1")
                .environmentId("env-1")
                .functionalityId("func-1")
                .build();
    }

    @Test
    void validate_acceptsValidDto() {
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
    void validate_throwsBusinessRule_whenCodeIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_040.getCode())).thenReturn("El codigo es requerido");
        CreateMessageDTO dto = validDto();
        dto.setCode("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El codigo es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_022.getCode())).thenReturn("El titulo es requerido");
        CreateMessageDTO dto = validDto();
        dto.setTitle("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El titulo es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsShorterThanTen() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_020.getCode())).thenReturn("Titulo corto");
        CreateMessageDTO dto = validDto();
        dto.setTitle("Short");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Titulo corto"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsLongerThanFifty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_021.getCode())).thenReturn("Titulo largo");
        CreateMessageDTO dto = validDto();
        dto.setTitle("a".repeat(51));

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Titulo largo"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_017.getCode())).thenReturn("El contenido es requerido");
        CreateMessageDTO dto = validDto();
        dto.setContent("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El contenido es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsShorterThanTen() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_018.getCode())).thenReturn("Contenido corto");
        CreateMessageDTO dto = validDto();
        dto.setContent("Short");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Contenido corto"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsLongerThanOneHundred() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_019.getCode())).thenReturn("Contenido largo");
        CreateMessageDTO dto = validDto();
        dto.setContent("a".repeat(101));

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Contenido largo"));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationIdIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn("El id de la aplicación es requerido.");
        CreateMessageDTO dto = validDto();
        dto.setApplicationId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenEnvironmentIdIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_171.getCode()))
                .thenReturn("El id del entorno es requerido.");
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id del entorno es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenFunctionalityIdIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_172.getCode()))
                .thenReturn("El id de la funcionalidad es requerido.");
        CreateMessageDTO dto = validDto();
        dto.setFunctionalityId("");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la funcionalidad es requerido."));
    }
}
package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageCompositeValidatorTest {

    private static final String TYPE_ID = "123e4567-e89b-12d3-a456-426614175101";
    private static final String CATEGORY_ID = "123e4567-e89b-12d3-a456-426614175102";
    private static final String STATUS_ID = "123e4567-e89b-12d3-a456-426614175103";
    private static final String MESSAGE_ENVIRONMENT_STATE_ID = "123e4567-e89b-12d3-a456-426614175104";
    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175105";
    private static final String FUNCTIONALITY_ID = "123e4567-e89b-12d3-a456-426614175106";
    private static final String DEFAULT_UUID = "00000000-0000-0000-0000-000000000000";
    private static final String MISSING_ID = "123e4567-e89b-12d3-a456-426614175199";
    private static final String INVALID_UUID = "not-a-uuid";
    private static final String UUID_FORMAT_MESSAGE = "The UUID to be converted has no valid format.";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private CreateMessageContextRule contextRule;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;

    private CreateMessageCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateMessageCompositeValidator(catalogPort, contextRule,
                recordExistsCatalogPort, new UUIDValidator(catalogPort));
    }

    private CreateMessageDTO validDto() {
        return CreateMessageDTO.builder()
                .code("MSG-001")
                .title("A valid title")
                .content("A valid message content")
                .typeId(TYPE_ID)
                .categoryId(CATEGORY_ID)
                .statusId(STATUS_ID)
                .applicationId(APPLICATION_ID)
                .application("App")
                .functionalityId(FUNCTIONALITY_ID)
                .environmentId("env-1")
                .messageEnvironmentStateId(MESSAGE_ENVIRONMENT_STATE_ID)
                .build();
    }

    private static Stream<Arguments> catalogReferenceFields() {
        return Stream.of(
                Arguments.of(ReferenceCatalog.MESSAGE_TYPE, "El tipo de mensaje",
                        (BiConsumer<CreateMessageDTO, String>) CreateMessageDTO::setTypeId),
                Arguments.of(ReferenceCatalog.MESSAGE_CATEGORY, "La categoría del mensaje",
                        (BiConsumer<CreateMessageDTO, String>) CreateMessageDTO::setCategoryId),
                Arguments.of(ReferenceCatalog.MESSAGE_STATE, "El estado del mensaje",
                        (BiConsumer<CreateMessageDTO, String>) CreateMessageDTO::setStatusId),
                Arguments.of(ReferenceCatalog.MESSAGE_ENVIRONMENT_STATE, "El estado del mensaje en el ambiente",
                        (BiConsumer<CreateMessageDTO, String>) CreateMessageDTO::setMessageEnvironmentStateId));
    }

    @Test
    void validate_acceptsValidDto() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();

        assertDoesNotThrow(() -> validator.validate(dto, "env-1"));

        verify(contextRule).validate(dto, "env-1");
    }

    @Test
    void validate_checksEveryMessageCatalogReferenceInOrderBeforeDelegatingToContextRule() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();

        validator.validate(dto, "env-1");

        InOrder catalogOrder = inOrder(recordExistsCatalogPort, contextRule);
        catalogOrder.verify(recordExistsCatalogPort).exists(ReferenceCatalog.MESSAGE_TYPE, TYPE_ID);
        catalogOrder.verify(recordExistsCatalogPort).exists(ReferenceCatalog.MESSAGE_CATEGORY, CATEGORY_ID);
        catalogOrder.verify(recordExistsCatalogPort).exists(ReferenceCatalog.MESSAGE_STATE, STATUS_ID);
        catalogOrder.verify(recordExistsCatalogPort)
                .exists(ReferenceCatalog.MESSAGE_ENVIRONMENT_STATE, MESSAGE_ENVIRONMENT_STATE_ID);
        catalogOrder.verify(contextRule).validate(dto, "env-1");
    }

    @Test
    void validate_acceptsDto_whenBodyEnvironmentIdIsAbsent() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId("");

        assertDoesNotThrow(() -> validator.validate(dto, "env-1"));

        verify(contextRule).validate(dto, "env-1");
    }

    @Test
    void validate_delegatesBodyAndAuthenticatedEnvironmentWhenTheyDiffer() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId("other-env");

        assertDoesNotThrow(() -> validator.validate(dto, "env-1"));

        verify(contextRule).validate(dto, "env-1");
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("catalogReferenceFields")
    void validate_throwsBusinessRule_whenCatalogReferenceIsRequired(ReferenceCatalog catalog, String fieldName,
                                                                     BiConsumer<CreateMessageDTO, String> setter) {
        lenient().when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();
        setter.accept(dto, "");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo(fieldName + " es requerido."));
        verify(recordExistsCatalogPort, never()).exists(eq(catalog), anyString());
        verifyNoInteractions(contextRule);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("catalogReferenceFields")
    void validate_throwsBusinessRule_whenCatalogReferenceIsNotUuid(ReferenceCatalog catalog, String fieldName,
                                                                    BiConsumer<CreateMessageDTO, String> setter) {
        lenient().when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        CreateMessageDTO dto = validDto();
        setter.accept(dto, INVALID_UUID);

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo(UUID_FORMAT_MESSAGE));
        verify(recordExistsCatalogPort, never()).exists(eq(catalog), eq(INVALID_UUID));
        verifyNoInteractions(contextRule);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("catalogReferenceFields")
    void validate_throwsBusinessRule_whenCatalogReferenceDoesNotExist(ReferenceCatalog catalog, String fieldName,
                                                                      BiConsumer<CreateMessageDTO, String> setter) {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(recordExistsCatalogPort.exists(catalog, MISSING_ID)).thenReturn(false);
        CreateMessageDTO dto = validDto();
        setter.accept(dto, MISSING_ID);

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo(fieldName + " no existe."));
        verifyNoInteractions(contextRule);
    }

    @Test
    void validate_throwsBusinessRule_whenCatalogReferenceIsDefaultUuid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn("UUID no válido");
        CreateMessageDTO dto = validDto();
        dto.setTypeId(DEFAULT_UUID);

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getTechnicalMessage())
                        .isEqualTo("UUID no válido"));
        verifyNoInteractions(recordExistsCatalogPort, contextRule);
    }

    @Test
    void validate_throwsBusinessRule_whenDtoIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())).thenReturn("Datos no validos");

        assertThatThrownBy(() -> validator.validate(null, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Datos no validos"));
    }

    @Test
    void validate_throwsBusinessRule_whenCodeIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_040.getCode())).thenReturn("El codigo es requerido");
        CreateMessageDTO dto = validDto();
        dto.setCode("");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El codigo es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_022.getCode())).thenReturn("El titulo es requerido");
        CreateMessageDTO dto = validDto();
        dto.setTitle("");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El titulo es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsShorterThanTen() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_020.getCode())).thenReturn("Titulo corto");
        CreateMessageDTO dto = validDto();
        dto.setTitle("Short");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Titulo corto"));
    }

    @Test
    void validate_throwsBusinessRule_whenTitleIsLongerThanFifty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_021.getCode())).thenReturn("Titulo largo");
        CreateMessageDTO dto = validDto();
        dto.setTitle("a".repeat(51));

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Titulo largo"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_017.getCode())).thenReturn("El contenido es requerido");
        CreateMessageDTO dto = validDto();
        dto.setContent("");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El contenido es requerido"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsShorterThanTen() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_018.getCode())).thenReturn("Contenido corto");
        CreateMessageDTO dto = validDto();
        dto.setContent("Short");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Contenido corto"));
    }

    @Test
    void validate_throwsBusinessRule_whenContentIsLongerThanOneHundred() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_019.getCode())).thenReturn("Contenido largo");
        CreateMessageDTO dto = validDto();
        dto.setContent("a".repeat(101));

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Contenido largo"));
    }

    @Test
    void validate_throwsBusinessRule_whenApplicationIdIsEmpty() {
        CreateMessageDTO dto = validDto();
        dto.setApplicationId("");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la aplicación es requerido."));
    }

    @Test
    void validate_throwsBusinessRule_whenFunctionalityIdIsEmpty() {
        CreateMessageDTO dto = validDto();
        dto.setFunctionalityId("");

        assertThatThrownBy(() -> validator.validate(dto, "env-1"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El id de la funcionalidad es requerido."));
    }
}

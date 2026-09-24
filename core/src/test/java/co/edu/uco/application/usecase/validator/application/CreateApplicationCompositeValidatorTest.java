package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CreateApplicationCompositeValidatorTest {

    private static final String LANGUAGE_ID = "123e4567-e89b-12d3-a456-426614175301";
    private static final String STATE_ID = "123e4567-e89b-12d3-a456-426614175302";
    private static final String INVALID_UUID = "not-a-uuid";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CreateApplicationOrganizationExistsRule organizationExistsRule;

    private CreateApplicationCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateApplicationCompositeValidator(
                catalogPort, recordExistsCatalogPort, applicationRepository, new DateValidValidator(catalogPort),
                organizationExistsRule);
    }

    private CreateApplicationDTO validDto() {
        return CreateApplicationDTO.builder()
                .name("Message App")
                .organizationId("123e4567-e89b-12d3-a456-426614174000")
                .languageId(LANGUAGE_ID)
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId(STATE_ID)
                .build();
    }

    @Test
    void validate_acceptsValidDto() {
        when(recordExistsCatalogPort.exists(any(), anyString())).thenReturn(true);
        when(applicationRepository.findByName("Message App")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validate(validDto()));
    }

    @Test
    void validate_invokesOrganizationRuleBeforeCatalogAndDuplicateChecks() {
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(ReferenceCatalog.APPLICATION_STATE, STATE_ID)).thenReturn(true);
        when(applicationRepository.findByName("Message App")).thenReturn(Optional.empty());
        CreateApplicationDTO dto = validDto();

        validator.validate(dto);

        InOrder validationOrder = inOrder(organizationExistsRule, recordExistsCatalogPort, applicationRepository);
        validationOrder.verify(organizationExistsRule).validate(dto);
        validationOrder.verify(recordExistsCatalogPort).exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID);
        validationOrder.verify(recordExistsCatalogPort).exists(ReferenceCatalog.APPLICATION_STATE, STATE_ID);
        validationOrder.verify(applicationRepository).findByName("Message App");
    }

    @Test
    void validate_shortCircuitsSubsequentChecks_whenOrganizationRuleFails() {
        CreateApplicationDTO dto = validDto();
        BusinessRuleException failure = BusinessRuleException.buildUserException("Organización inválida");
        doThrow(failure).when(organizationExistsRule).validate(dto);

        assertThatThrownBy(() -> validator.validate(dto)).isSameAs(failure);

        verify(organizationExistsRule).validate(dto);
        verifyNoInteractions(recordExistsCatalogPort, applicationRepository);
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
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID)).thenReturn(false);
        CreateApplicationDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El idioma de la aplicación no existe."));
        verify(recordExistsCatalogPort).exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID);
        verify(recordExistsCatalogPort, never()).exists(eq(ReferenceCatalog.APPLICATION_STATE), anyString());
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void validate_throwsBusinessRule_whenLanguageIdIsNotUuid() {
        CreateApplicationDTO dto = validDto();
        dto.setLanguageId(INVALID_UUID);

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El idioma de la aplicación no existe."));
        verifyNoInteractions(recordExistsCatalogPort, applicationRepository);
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
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID)).thenReturn(true);
        when(recordExistsCatalogPort.exists(ReferenceCatalog.APPLICATION_STATE, STATE_ID)).thenReturn(false);
        CreateApplicationDTO dto = validDto();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la aplicación no existe."));
        verify(recordExistsCatalogPort).exists(ReferenceCatalog.APPLICATION_STATE, STATE_ID);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void validate_throwsBusinessRule_whenStateIdIsNotUuid() {
        when(recordExistsCatalogPort.exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID)).thenReturn(true);
        CreateApplicationDTO dto = validDto();
        dto.setStateId(INVALID_UUID);

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El estado de la aplicación no existe."));
        verify(recordExistsCatalogPort).exists(ReferenceCatalog.LANGUAGE_BASE, LANGUAGE_ID);
        verify(recordExistsCatalogPort, never()).exists(eq(ReferenceCatalog.APPLICATION_STATE), anyString());
        verifyNoInteractions(applicationRepository);
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

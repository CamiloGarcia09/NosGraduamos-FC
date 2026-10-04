package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvironmentTypeDuplicatedRuleTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175000";
    private static final String TYPE_ID = "123e4567-e89b-12d3-a456-426614175501";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private EnvironmentTypeDuplicatedRule rule;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(anyString())).thenReturn("user message");
        rule = new EnvironmentTypeDuplicatedRule(catalogPort, environmentRepository);
    }

    private CreateEnvironmentDTO dto() {
        return CreateEnvironmentDTO.builder()
                .applicationId(APPLICATION_ID)
                .typeId(TYPE_ID)
                .build();
    }

    @Test
    void validate_doesNotThrow_whenApplicationAndTypeCombinationIsNotRegistered() {
        when(environmentRepository.existsByApplicationIdAndTypeId(APPLICATION_ID, TYPE_ID)).thenReturn(false);

        assertDoesNotThrow(() -> rule.validate(dto()));

        verify(environmentRepository).existsByApplicationIdAndTypeId(APPLICATION_ID, TYPE_ID);
    }

    @Test
    void validate_throwsBusinessRuleException_whenTypeAlreadyExistsForApplication() {
        when(environmentRepository.existsByApplicationIdAndTypeId(APPLICATION_ID, TYPE_ID)).thenReturn(true);

        assertThatThrownBy(() -> rule.validate(dto()))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat(((BusinessRuleException) exception).getUserMessage())
                        .isEqualTo("user message"));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_178.getCode());
        verify(environmentRepository).existsByApplicationIdAndTypeId(APPLICATION_ID, TYPE_ID);
    }

    @Test
    void validate_consultsRepositoryWithMissingIdentifiers_whenDtoOnlyHoldsEmptyValues() {
        when(environmentRepository.existsByApplicationIdAndTypeId("", "")).thenReturn(false);

        assertDoesNotThrow(() -> rule.validate(new CreateEnvironmentDTO()));

        verify(environmentRepository).existsByApplicationIdAndTypeId("", "");
    }
}

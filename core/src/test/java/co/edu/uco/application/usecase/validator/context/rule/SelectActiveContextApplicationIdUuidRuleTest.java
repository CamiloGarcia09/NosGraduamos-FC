package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextApplicationIdUuidRuleTest {

    private static final String VALID_UUID = "123e4567-e89b-12d3-a456-426614175002";
    private static final String MALFORMED_UUID = "not-a-uuid";
    private static final String UUID_MESSAGE = "The application id is not a valid UUID.";

    @Mock
    private CatalogPort catalogPort;

    private SelectActiveContextApplicationIdUuidRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextApplicationIdUuidRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenApplicationIdIsValidUuid() {
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .applicationId(VALID_UUID).build();

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleUsingFun038_whenApplicationIdIsMalformed() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn(UUID_MESSAGE);
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .applicationId(MALFORMED_UUID).build();

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getUserMessage).isEqualTo(UUID_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_038.getCode());
    }

    @ParameterizedTest(name = "applicationId=[{0}]")
    @ValueSource(strings = {"", "   ", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsBusinessRuleUsingFun038_whenApplicationIdIsBlankOrDefault(String applicationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn(UUID_MESSAGE);
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .applicationId(applicationId).build();

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validate_throwsBusinessRule_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode())).thenReturn(UUID_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(BusinessRuleException.class);
    }
}

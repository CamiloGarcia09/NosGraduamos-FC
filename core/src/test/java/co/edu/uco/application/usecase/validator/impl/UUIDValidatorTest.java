package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UUIDValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private UUIDValidator validator;

    @Test
    void validate_doesNotThrow_forValidUuid() {
        assertThatCode(() -> validator.validate(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenUuidIsDefault() {
        when(catalogPort.getMessage("FUN_038")).thenReturn("UUID cannot be empty");
        String defaultUuid = "00000000-0000-0000-0000-000000000000";

        assertThatThrownBy(() -> validator.validate(defaultUuid))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> {
                    assertThat(((BusinessRuleException) ex).getTechnicalMessage()).isEqualTo("UUID cannot be empty");
                });
    }

    @Test
    void validate_throwsBusinessRuleException_forBlankUuid() {
        when(catalogPort.getMessage("FUN_038")).thenReturn("UUID cannot be empty");

        assertThatThrownBy(() -> validator.validate("   "))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validate_throwsBusinessRuleException_forInvalidUuidFormat() {
        assertThatThrownBy(() -> validator.validate("not-a-uuid"))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("The UUID to be converted has no valid format."));
    }
}
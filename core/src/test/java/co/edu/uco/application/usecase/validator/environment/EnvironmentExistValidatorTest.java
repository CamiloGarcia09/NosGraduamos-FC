package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvironmentExistValidatorTest {

    @Mock
    private EnvironmentRepository repository;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private EnvironmentExistValidator validator;

    @Test
    void validate_doesNotThrow_whenEnvironmentExists() {
        when(repository.findById("env-123")).thenReturn(Optional.of(new co.edu.uco.application.secondaryports.entity.EnvironmentData()));
        CreateTokenDTO dto = new CreateTokenDTO("2025-01-01T10:00:00", "env-123");

        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenEnvironmentDoesNotExist() {
        when(catalogPort.getMessage("FUN_035")).thenReturn("Environment does not exist");
        when(repository.findById("env-123")).thenReturn(Optional.empty());
        CreateTokenDTO dto = new CreateTokenDTO("2025-01-01T10:00:00", "env-123");

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Environment does not exist"));
    }
}
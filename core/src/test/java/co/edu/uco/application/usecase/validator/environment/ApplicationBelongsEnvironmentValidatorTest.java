package co.edu.uco.application.usecase.validator.environment;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationBelongsEnvironmentValidatorTest {

    @Mock
    private EnvironmentRepository repository;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private ApplicationBelongsEnvironmentValidator validator;

    private final UUID applicationId = UUID.randomUUID();

    @Test
    void validate_doesNotThrow_whenEnvironmentBelongsToApplication() {
        ApplicationData application = new ApplicationData(applicationId, "app");
        EnvironmentData environment = new EnvironmentData(UUID.randomUUID(), "env", application);
        when(repository.findById(environment.getId().toString())).thenReturn(Optional.of(environment));

        assertThatCode(() -> validator.validate(environment.getId(), applicationId)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenEnvironmentBelongsToDifferentApplication() {
        ApplicationData application = new ApplicationData(applicationId, "app");
        EnvironmentData environment = new EnvironmentData(UUID.randomUUID(), "env", application);
        when(repository.findById(environment.getId().toString())).thenReturn(Optional.of(environment));

        assertThatThrownBy(() -> validator.validate(environment.getId(), UUID.randomUUID()))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentIsAbsent() {
        UUID envId = UUID.randomUUID();
        when(repository.findById(envId.toString())).thenReturn(Optional.empty());

        assertThatCode(() -> validator.validate(envId, UUID.randomUUID())).doesNotThrowAnyException();
    }
}
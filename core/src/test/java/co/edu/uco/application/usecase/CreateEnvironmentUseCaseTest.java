package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.environment.CreateEnvironmentCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEnvironmentUseCaseTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";

    @Mock
    private EnvironmentRepository environmentRepository;
    @Mock
    private CreateEnvironmentCompositeValidator validator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateEnvironmentUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateEnvironmentUseCase.class)).thenReturn(log);
        useCase = new CreateEnvironmentUseCase(environmentRepository, validator, loggerFactory);
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
    void createEnvironment_persistsEnvironmentAndLogs() {
        CreateEnvironmentDTO dto = validDto();

        useCase.createEnvironment(dto);

        verify(validator).validate(dto);
        verify(environmentRepository).create(any(), eq("type-1"), eq("state-1"));
        verify(log).info("Environment created successfully with name: {}", "Production");
    }

    @Test
    void createEnvironment_propagatesValidationError() {
        CreateEnvironmentDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid environment"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createEnvironment(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid environment"));
        verifyNoInteractions(environmentRepository);
    }

    @Test
    void createEnvironment_throwsBusinessException_whenRepositoryFails() {
        CreateEnvironmentDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(environmentRepository).create(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createEnvironment(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("Error al crear el entorno"));
        verify(log).error(eq("Error creating environment in repository"), any(RuntimeException.class));
    }

    @Test
    void createEnvironment_rethrowsCrossWordsExceptionFromRepository() {
        CreateEnvironmentDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("conflict"))
                .when(environmentRepository).create(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createEnvironment(dto))
                .isInstanceOf(BusinessRuleException.class);
    }
}
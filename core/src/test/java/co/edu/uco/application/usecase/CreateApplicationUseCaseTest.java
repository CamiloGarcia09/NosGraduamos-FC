package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
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
class CreateApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CreateApplicationCompositeValidator validator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateApplicationUseCase.class)).thenReturn(log);
        useCase = new CreateApplicationUseCase(applicationRepository, validator, loggerFactory);
    }

    private CreateApplicationDTO validDto() {
        return CreateApplicationDTO.builder()
                .name("Message App")
                .languageId("lang-1")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state-1")
                .build();
    }

    @Test
    void createApplication_persistsApplicationAndLogs() {
        CreateApplicationDTO dto = validDto();

        useCase.createApplication(dto);

        verify(validator).validate(dto);
        verify(applicationRepository).create(any(), eq("lang-1"), any(), any(), eq("state-1"));
        verify(log).info("Application created successfully with name: {}", "Message App");
    }

    @Test
    void createApplication_propagatesValidationError() {
        CreateApplicationDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid application"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createApplication(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid application"));
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void createApplication_throwsBusinessException_whenRepositoryFails() {
        CreateApplicationDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(applicationRepository).create(any(), anyString(), any(), any(), anyString());

        assertThatThrownBy(() -> useCase.createApplication(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("Error al crear la aplicación"));
        verify(log).error(eq("Error creating application in repository"), any(RuntimeException.class));
    }

    @Test
    void createApplication_rethrowsCrossWordsExceptionFromRepository() {
        CreateApplicationDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("conflict"))
                .when(applicationRepository).create(any(), anyString(), any(), any(), anyString());

        assertThatThrownBy(() -> useCase.createApplication(dto))
                .isInstanceOf(BusinessRuleException.class);
    }
}
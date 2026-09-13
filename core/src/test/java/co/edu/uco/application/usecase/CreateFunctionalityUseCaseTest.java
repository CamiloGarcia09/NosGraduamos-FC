package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.usecase.validator.functionality.CreateFunctionalityCompositeValidator;
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
class CreateFunctionalityUseCaseTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";

    @Mock
    private FunctionalityRepository functionalityRepository;
    @Mock
    private CreateFunctionalityCompositeValidator validator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateFunctionalityUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateFunctionalityUseCase.class)).thenReturn(log);
        useCase = new CreateFunctionalityUseCase(functionalityRepository, validator, loggerFactory);
    }

    private CreateFunctionalityDTO validDto() {
        return CreateFunctionalityDTO.builder()
                .name("Search messages")
                .applicationId(APP_UUID)
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state-1")
                .build();
    }

    @Test
    void createFunctionality_persistsFunctionalityAndLogs() {
        CreateFunctionalityDTO dto = validDto();

        useCase.createFunctionality(dto);

        verify(validator).validate(dto);
        verify(functionalityRepository).create(any(), eq("state-1"));
        verify(log).info("Functionality created successfully with name: {}", "Search messages");
    }

    @Test
    void createFunctionality_propagatesValidationError() {
        CreateFunctionalityDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid functionality"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createFunctionality(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid functionality"));
        verifyNoInteractions(functionalityRepository);
    }

    @Test
    void createFunctionality_throwsBusinessException_whenRepositoryFails() {
        CreateFunctionalityDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(functionalityRepository).create(any(), anyString());

        assertThatThrownBy(() -> useCase.createFunctionality(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("Error al crear la funcionalidad"));
        verify(log).error(eq("Error creating functionality in repository"), any(RuntimeException.class));
    }

    @Test
    void createFunctionality_rethrowsCrossWordsExceptionFromRepository() {
        CreateFunctionalityDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("conflict"))
                .when(functionalityRepository).create(any(), anyString());

        assertThatThrownBy(() -> useCase.createFunctionality(dto))
                .isInstanceOf(BusinessRuleException.class);
    }
}
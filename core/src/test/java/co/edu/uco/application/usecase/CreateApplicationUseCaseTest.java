package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreateApplicationUseCaseTest {

    private static final String ORGANIZATION_ID = "123e4567-e89b-12d3-a456-426614174000";

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
                .organizationId(ORGANIZATION_ID)
                .languageId("lang-1")
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId("state-1")
                .build();
    }

    @Test
    void createApplication_persistsApplicationAndLogs() {
        CreateApplicationDTO dto = validDto();
        ArgumentCaptor<ApplicationData> applicationCaptor = ArgumentCaptor.forClass(ApplicationData.class);

        useCase.createApplication(dto);

        verify(validator).validate(dto);
        verify(applicationRepository).create(applicationCaptor.capture(), eq("lang-1"), any(), any(), eq("state-1"));
        ApplicationData capturedApplication = applicationCaptor.getValue();
        assertSoftly(softly -> {
            softly.assertThat(capturedApplication.getName()).isEqualTo("Message App");
            softly.assertThat(capturedApplication.getOrganization().getId())
                    .isEqualTo(UUID.fromString(ORGANIZATION_ID));
            softly.assertThat(capturedApplication.getOrganization().getName()).isEmpty();
        });
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

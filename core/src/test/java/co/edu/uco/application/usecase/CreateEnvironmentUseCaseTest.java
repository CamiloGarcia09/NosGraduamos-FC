package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.environment.CreateEnvironmentCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEnvironmentUseCaseTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";
    private static final String OTHER_APP_UUID = "123e4567-e89b-12d3-a456-426614175002";
    private static final String TYPE_UUID = "123e4567-e89b-12d3-a456-426614175003";
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", Instant.MAX);

    @Mock
    private EnvironmentRepository environmentRepository;
    @Mock
    private CreateEnvironmentCompositeValidator validator;
    @Mock
    private HandlingActiveContextPort activeContextPort;
    @Mock
    private AuthorizationCompositeValidator authorizationCompositeValidator;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateEnvironmentUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateEnvironmentUseCase.class)).thenReturn(log);
        useCase = new CreateEnvironmentUseCase(environmentRepository, validator,
                activeContextPort, authorizationCompositeValidator, catalogPort, loggerFactory);
    }

    private CreateEnvironmentDTO validDto() {
        return CreateEnvironmentDTO.builder()
                .applicationId(APP_UUID)
                .typeId(TYPE_UUID)
                .stateId("state-1")
                .build();
    }

    @Test
    void createEnvironment_persistsLegacyEnvironmentWhenIdentityIsNull() {
        CreateEnvironmentDTO dto = validDto();
        ArgumentCaptor<EnvironmentData> environmentCaptor = ArgumentCaptor.forClass(EnvironmentData.class);

        useCase.createEnvironment(dto, null);

        verify(validator).validate(dto);
        verify(environmentRepository).create(environmentCaptor.capture(), eq(TYPE_UUID), eq("state-1"));
        EnvironmentData environment = environmentCaptor.getValue();
        assertSoftly(softly -> {
            softly.assertThat(environment.getId()).isNotNull();
            softly.assertThat(environment.getApplication().getId()).isEqualTo(UUID.fromString(APP_UUID));
            softly.assertThat(environment.getType().getId()).isEqualTo(UUID.fromString(TYPE_UUID));
            softly.assertThat(environment.getType().getName()).isEmpty();
        });
        verify(log).info("Environment created successfully with type id: {}", TYPE_UUID);
        verifyNoInteractions(activeContextPort, authorizationCompositeValidator, catalogPort);
    }

    @Test
    void createEnvironment_authorizesMatchingActiveContextWithEnvironmentCreatePermission() {
        CreateEnvironmentDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(APP_UUID).build());

        useCase.createEnvironment(dto, IDENTITY);

        verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.ENVIRONMENT_CREATE,
                AuthorizationScopeType.APPLICATION, UUID.fromString(APP_UUID));
        verify(validator).validate(dto);
        verify(environmentRepository).create(any(), eq(TYPE_UUID), eq("state-1"));
        verify(log).info("Environment created successfully with type id: {}", TYPE_UUID);
        verifyNoInteractions(catalogPort);
    }

    @Test
    void createEnvironment_throwsForbidden_whenActiveContextDoesNotExist() {
        CreateEnvironmentDTO dto = validDto();
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createEnvironment(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(environmentRepository, authorizationCompositeValidator);
        verify(validator, never()).validate(any());
    }

    @Test
    void createEnvironment_throwsForbidden_whenActiveContextIsOutsideRequestedApplication() {
        CreateEnvironmentDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(OTHER_APP_UUID).build());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createEnvironment(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(environmentRepository, authorizationCompositeValidator, validator);
    }

    @Test
    void createEnvironment_propagatesForbiddenFromAuthorizationCompositeValidator() {
        CreateEnvironmentDTO dto = validDto();
        ForbiddenException failure = ForbiddenException.buildUserException("Permission denied");
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(APP_UUID).build());
        doThrow(failure).when(authorizationCompositeValidator).validate(IDENTITY,
                PermissionCode.ENVIRONMENT_CREATE, AuthorizationScopeType.APPLICATION,
                UUID.fromString(APP_UUID));

        assertThatThrownBy(() -> useCase.createEnvironment(dto, IDENTITY)).isSameAs(failure);
        verifyNoInteractions(environmentRepository, validator);
    }

    @Test
    void createEnvironment_propagatesValidationError() {
        CreateEnvironmentDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid environment"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createEnvironment(dto, null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid environment"));
        verifyNoInteractions(environmentRepository);
    }

    @Test
    void createEnvironment_throwsBusinessException_whenRepositoryFails() {
        CreateEnvironmentDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(environmentRepository).create(any(), anyString(), anyString());

        assertThatThrownBy(() -> useCase.createEnvironment(dto, null))
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

        assertThatThrownBy(() -> useCase.createEnvironment(dto, null))
                .isInstanceOf(BusinessRuleException.class);
    }
}

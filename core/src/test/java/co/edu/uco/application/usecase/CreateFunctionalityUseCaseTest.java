package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.FunctionalityRepository;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationCompositeValidator;
import co.edu.uco.application.usecase.validator.functionality.CreateFunctionalityCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFunctionalityUseCaseTest {

    private static final String APP_UUID = "123e4567-e89b-12d3-a456-426614175000";
    private static final String OTHER_APP_UUID = "123e4567-e89b-12d3-a456-426614175002";
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

    @Mock
    private FunctionalityRepository functionalityRepository;
    @Mock
    private CreateFunctionalityCompositeValidator validator;
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

    private CreateFunctionalityUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateFunctionalityUseCase.class)).thenReturn(log);
        useCase = new CreateFunctionalityUseCase(functionalityRepository, validator,
                activeContextPort, authorizationCompositeValidator, catalogPort, loggerFactory);
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
    void createFunctionality_persistsLegacyFunctionalityWhenIdentityIsNull() {
        CreateFunctionalityDTO dto = validDto();

        useCase.createFunctionality(dto, null);

        verify(validator).validate(dto);
        verify(functionalityRepository).create(any(), eq("state-1"));
        verify(log).info("Functionality created successfully with name: {}", "Search messages");
        verifyNoInteractions(activeContextPort, authorizationCompositeValidator, catalogPort);
    }

    @Test
    void createFunctionality_authorizesMatchingActiveContextWithFunctionalityCreatePermission() {
        CreateFunctionalityDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(APP_UUID).build());

        useCase.createFunctionality(dto, IDENTITY);

        verify(authorizationCompositeValidator).validate(IDENTITY, PermissionCode.FUNCTIONALITY_CREATE,
                AuthorizationScopeType.APPLICATION, UUID.fromString(APP_UUID));
        verify(validator).validate(dto);
        verify(functionalityRepository).create(any(), eq("state-1"));
        verify(log).info("Functionality created successfully with name: {}", "Search messages");
        verifyNoInteractions(catalogPort);
    }

    @Test
    void createFunctionality_throwsForbidden_whenActiveContextDoesNotExist() {
        CreateFunctionalityDTO dto = validDto();
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createFunctionality(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(functionalityRepository, authorizationCompositeValidator);
        verify(validator, never()).validate(any());
    }

    @Test
    void createFunctionality_throwsForbidden_whenActiveContextIsOutsideRequestedApplication() {
        CreateFunctionalityDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(OTHER_APP_UUID).build());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createFunctionality(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(functionalityRepository, authorizationCompositeValidator, validator);
    }

    @Test
    void createFunctionality_propagatesForbiddenFromAuthorizationCompositeValidator() {
        CreateFunctionalityDTO dto = validDto();
        ForbiddenException failure = ForbiddenException.buildUserException("Permission denied");
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().applicationId(APP_UUID).build());
        doThrow(failure).when(authorizationCompositeValidator).validate(IDENTITY,
                PermissionCode.FUNCTIONALITY_CREATE, AuthorizationScopeType.APPLICATION,
                UUID.fromString(APP_UUID));

        assertThatThrownBy(() -> useCase.createFunctionality(dto, IDENTITY)).isSameAs(failure);
        verifyNoInteractions(functionalityRepository, validator);
    }

    @Test
    void createFunctionality_propagatesValidationError() {
        CreateFunctionalityDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid functionality"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createFunctionality(dto, null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid functionality"));
        verifyNoInteractions(functionalityRepository);
    }

    @Test
    void createFunctionality_throwsBusinessException_whenRepositoryFails() {
        CreateFunctionalityDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(functionalityRepository).create(any(), anyString());

        assertThatThrownBy(() -> useCase.createFunctionality(dto, null))
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

        assertThatThrownBy(() -> useCase.createFunctionality(dto, null))
                .isInstanceOf(BusinessRuleException.class);
    }
}

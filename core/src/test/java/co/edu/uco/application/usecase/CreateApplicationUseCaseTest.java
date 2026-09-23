package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRule;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreateApplicationUseCaseTest {

    private static final String ORGANIZATION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String OTHER_ORGANIZATION_ID = "123e4567-e89b-12d3-a456-426614174002";
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CreateApplicationCompositeValidator validator;
    @Mock
    private HandlingActiveContextPort activeContextPort;
    @Mock
    private AuthorizationRule authorizationRule;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private CreateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CreateApplicationUseCase.class)).thenReturn(log);
        useCase = new CreateApplicationUseCase(applicationRepository, validator,
                activeContextPort, authorizationRule, catalogPort, loggerFactory);
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
    void createApplication_persistsLegacyApplicationWhenIdentityIsNull() {
        CreateApplicationDTO dto = validDto();
        ArgumentCaptor<ApplicationData> applicationCaptor = ArgumentCaptor.forClass(ApplicationData.class);

        useCase.createApplication(dto, null);

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
        verifyNoInteractions(activeContextPort, authorizationRule, catalogPort);
    }

    @Test
    void createApplication_authorizesMatchingActiveContextWithApplicationCreatePermission() {
        CreateApplicationDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().organizationId(ORGANIZATION_ID).build());

        useCase.createApplication(dto, IDENTITY);

        verify(authorizationRule).validate(IDENTITY, PermissionCode.APPLICATION_CREATE,
                AuthorizationScopeType.ORGANIZATION, UUID.fromString(ORGANIZATION_ID));
        verify(validator).validate(dto);
        verify(applicationRepository).create(any(ApplicationData.class),
                eq("lang-1"), any(), any(), eq("state-1"));
        verifyNoInteractions(catalogPort);
    }

    @Test
    void createApplication_throwsForbidden_whenActiveContextDoesNotExist() {
        CreateApplicationDTO dto = validDto();
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createApplication(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(applicationRepository, authorizationRule);
        verify(validator, never()).validate(any());
    }

    @Test
    void createApplication_throwsForbidden_whenActiveContextIsOutsideRequestedOrganization() {
        CreateApplicationDTO dto = validDto();
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().organizationId(OTHER_ORGANIZATION_ID).build());
        when(catalogPort.getMessage("FUN_153")).thenReturn("Fuera del contexto activo");

        assertThatThrownBy(() -> useCase.createApplication(dto, IDENTITY))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(ex -> assertThat((ForbiddenException) ex)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Fuera del contexto activo", 403));
        verifyNoInteractions(applicationRepository, authorizationRule, validator);
    }

    @Test
    void createApplication_propagatesForbiddenFromAuthorizationRule() {
        CreateApplicationDTO dto = validDto();
        ForbiddenException failure = ForbiddenException.buildUserException("Permission denied");
        when(activeContextPort.findActiveContext(IDENTITY)).thenReturn(
                ActiveContextDTO.builder().organizationId(ORGANIZATION_ID).build());
        doThrow(failure).when(authorizationRule).validate(IDENTITY,
                PermissionCode.APPLICATION_CREATE, AuthorizationScopeType.ORGANIZATION,
                UUID.fromString(ORGANIZATION_ID));

        assertThatThrownBy(() -> useCase.createApplication(dto, IDENTITY)).isSameAs(failure);
        verifyNoInteractions(applicationRepository, validator);
    }

    @Test
    void createApplication_propagatesValidationError() {
        CreateApplicationDTO dto = validDto();
        doThrow(BusinessRuleException.buildUserException("Invalid application"))
                .when(validator).validate(dto);

        assertThatThrownBy(() -> useCase.createApplication(dto, null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Invalid application"));
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void createApplication_throwsBusinessException_whenRepositoryFails() {
        CreateApplicationDTO dto = validDto();
        doThrow(new RuntimeException("db down")).when(applicationRepository).create(any(), anyString(), any(), any(), anyString());

        assertThatThrownBy(() -> useCase.createApplication(dto, null))
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

        assertThatThrownBy(() -> useCase.createApplication(dto, null))
                .isInstanceOf(BusinessRuleException.class);
    }
}

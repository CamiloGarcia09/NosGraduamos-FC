package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationCompositeValidatorTest {

    private static final UUID SCOPE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", null, Instant.MAX);

    @Mock
    private AuthorizationQueryPort authorizationQueryPort;
    @Mock
    private CatalogPort catalogPort;

    private AuthorizationCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AuthorizationCompositeValidator(authorizationQueryPort, catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenPermissionIsGranted() {
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenReturn(true);

        assertThatCode(() -> validator.validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).doesNotThrowAnyException();

        verify(authorizationQueryPort).hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID);
    }

    @ParameterizedTest(name = "[{0}] scope is delegated to the query port")
    @EnumSource(AuthorizationScopeType.class)
    void validate_delegatesEveryScopeType_whenIdentityIsPresent(AuthorizationScopeType scopeType) {
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.MESSAGE_CREATE, scopeType, SCOPE_ID))
                .thenReturn(true);

        assertThatCode(() -> validator.validate(IDENTITY, PermissionCode.MESSAGE_CREATE, scopeType, SCOPE_ID))
                .doesNotThrowAnyException();

        verify(authorizationQueryPort).hasPermission(IDENTITY, PermissionCode.MESSAGE_CREATE, scopeType, SCOPE_ID);
    }

    @Test
    void validate_throwsUnauthorizedUsingFun152_andSkipsPermissionQuery_whenIdentityIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_152.getCode()))
                .thenReturn("Authentication required");

        assertThatThrownBy(() -> validator.validate(null, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(exception -> assertThat((UnauthorizedException) exception)
                        .extracting(UnauthorizedException::getHttpStatus, UnauthorizedException::getUserMessage)
                        .containsExactly(401, "Authentication required"));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_152.getCode());
        verify(authorizationQueryPort, never()).hasPermission(any(), any(), any(), any());
    }

    @Test
    void validate_throwsForbiddenUsingFun153_whenPermissionIsDenied() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()))
                .thenReturn("Permission denied");
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenReturn(false);

        assertThatThrownBy(() -> validator.validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, "Permission denied"));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_153.getCode());
        verify(catalogPort, never()).getMessage(MessageCatalogCodeEnum.FUN_152.getCode());
    }

    @Test
    void validate_throwsBusinessRuleUsingFun010_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode()))
                .thenReturn("Invalid authorization request");

        assertThatThrownBy(() -> validator.validate((AuthorizationValidationContext) null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getHttpStatus, BusinessRuleException::getUserMessage)
                        .containsExactly(422, "Invalid authorization request"));

        verifyNoInteractions(authorizationQueryPort);
    }

    @Test
    void validate_propagatesTechnicalFailureUnchanged_whenQueryPortFails() {
        BusinessException technicalFailure = BusinessException.buildTechnicalException(
                "authorization unavailable", new IllegalStateException("database unavailable"),
                ExceptionLocation.INFRASTRUCTURE);
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenThrow(technicalFailure);

        BusinessException thrown = assertThrows(BusinessException.class, () -> validator.validate(
                IDENTITY, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, SCOPE_ID));

        assertSame(technicalFailure, thrown);
    }

    @Test
    void validate_propagatesTechnicalFailureUnchanged_whenIdentityIsNullAndCatalogFails() {
        BusinessException technicalFailure = BusinessException.buildTechnicalException(
                "catalog unavailable", new IllegalStateException("redis down"), ExceptionLocation.INFRASTRUCTURE);
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_152.getCode())).thenThrow(technicalFailure);

        BusinessException thrown = assertThrows(BusinessException.class, () -> validator.validate(
                null, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, SCOPE_ID));

        assertSame(technicalFailure, thrown);
        verify(authorizationQueryPort, never())
                .hasPermission(any(), any(), any(), eq(SCOPE_ID));
    }
}

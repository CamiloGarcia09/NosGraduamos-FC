package co.edu.uco.application.usecase.validator.authorization.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationPermissionRuleTest {

    private static final UUID SCOPE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", null, Instant.MAX);

    @Mock
    private AuthorizationQueryPort authorizationQueryPort;
    @Mock
    private CatalogPort catalogPort;

    private AuthorizationPermissionRule rule;

    @BeforeEach
    void setUp() {
        rule = new AuthorizationPermissionRule(catalogPort, authorizationQueryPort);
    }

    @Test
    void validate_doesNotThrow_whenPermissionIsGranted() {
        AuthorizationValidationContext context = context(SCOPE_ID);
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenReturn(true);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();

        verify(authorizationQueryPort).hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID);
    }

    @Test
    void validate_delegatesNullScopeIdWithoutFailure_whenScopeIdIsNull() {
        AuthorizationValidationContext context = context(null);
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, null)).thenReturn(true);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();

        verify(authorizationQueryPort).hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, null);
    }

    @Test
    void validate_throwsForbiddenUsingFun153_whenPermissionIsDenied() {
        AuthorizationValidationContext context = context(SCOPE_ID);
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_153.getCode()))
                .thenReturn("Permission denied");
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenReturn(false);

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, "Permission denied"));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_153.getCode());
    }

    @Test
    void validate_propagatesTechnicalFailureUnchanged_whenQueryPortFails() {
        AuthorizationValidationContext context = context(SCOPE_ID);
        BusinessException technicalFailure = BusinessException.buildTechnicalException(
                "authorization unavailable", new IllegalStateException("database unavailable"),
                ExceptionLocation.INFRASTRUCTURE);
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenThrow(technicalFailure);

        BusinessException thrown = assertThrows(BusinessException.class, () -> rule.validate(context));

        assertSame(technicalFailure, thrown);
    }

    private static AuthorizationValidationContext context(UUID scopeId) {
        return new AuthorizationValidationContext(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, scopeId);
    }
}

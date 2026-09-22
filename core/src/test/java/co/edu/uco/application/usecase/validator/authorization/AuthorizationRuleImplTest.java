package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationRuleImplTest {

    private static final UUID SCOPE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final ExternalIdentity IDENTITY = new ExternalIdentity(
            "issuer", "subject", null, PrincipalType.HUMAN, Instant.MAX);

    @Mock
    private AuthorizationQueryPort authorizationQueryPort;
    @Mock
    private CatalogPort catalogPort;

    private AuthorizationRuleImpl rule;

    @BeforeEach
    void setUp() {
        rule = new AuthorizationRuleImpl(authorizationQueryPort, catalogPort);
    }

    @Test
    void validate_allowsIdentityWhenPermissionExists() {
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenReturn(true);

        assertThatCode(() -> rule.validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).doesNotThrowAnyException();
        verify(authorizationQueryPort).hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID);
    }

    @Test
    void validate_throwsUnauthorizedWithCatalogMessageWhenIdentityIsNull() {
        when(catalogPort.getMessage("FUN_152")).thenReturn("Authentication required");

        assertThatThrownBy(() -> rule.validate(null, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID))
                .isInstanceOf(UnauthorizedException.class)
                .extracting("httpStatus", "userMessage")
                .containsExactly(401, "Authentication required");
        verify(authorizationQueryPort, never()).hasPermission(null, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID);
    }

    @Test
    void validate_throwsForbiddenWithCatalogMessageWhenPermissionIsDenied() {
        when(catalogPort.getMessage("FUN_153")).thenReturn("Permission denied");

        assertThatThrownBy(() -> rule.validate(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID))
                .isInstanceOf(ForbiddenException.class)
                .extracting("httpStatus", "userMessage")
                .containsExactly(403, "Permission denied");
    }

    @Test
    void validate_propagatesTechnicalFailureUnchanged() {
        BusinessException technicalFailure = BusinessException.buildTechnicalException(
                "authorization unavailable", new IllegalStateException("database unavailable"),
                ExceptionLocation.INFRASTRUCTURE);
        when(authorizationQueryPort.hasPermission(IDENTITY, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.APPLICATION, SCOPE_ID)).thenThrow(technicalFailure);

        BusinessException thrown = assertThrows(BusinessException.class, () -> rule.validate(
                IDENTITY, PermissionCode.CONTEXT_SELECT, AuthorizationScopeType.APPLICATION, SCOPE_ID));

        assertSame(technicalFailure, thrown);
    }
}

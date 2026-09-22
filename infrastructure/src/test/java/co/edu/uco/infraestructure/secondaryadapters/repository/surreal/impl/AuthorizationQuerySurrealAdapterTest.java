package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationQuerySurrealAdapterTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private AuthorizationQuerySurrealAdapter adapter;
    private ExternalIdentity identity;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(AuthorizationQuerySurrealAdapter.class)).thenReturn(log);
        adapter = new AuthorizationQuerySurrealAdapter(surreal, loggerFactory);
        identity = new ExternalIdentity("issuer", "subject", null, PrincipalType.HUMAN, Instant.MAX);
    }

    @Test
    void hasPermission_returnsTrueForApplicationScopeAndIncludesInheritedScopeChecks() {
        UUID applicationId = UUID.randomUUID();
        doReturn(responseWith(mock(Object.class))).when(surreal).query(anyString());

        boolean result = adapter.hasPermission(identity, PermissionCode.MESSAGE_READ,
                AuthorizationScopeType.APPLICATION, applicationId);

        assertThat(result).isTrue();
        assertThat(capturedQuery())
                .contains("issuer = 'issuer' AND subject = 'subject'")
                .contains("membership WHERE identity_id IN")
                .contains("membership_id.organization_id = (SELECT VALUE organization_id FROM application:`"
                        + applicationId + "`)[0]")
                .contains("permission WHERE code = 'MESSAGE_READ'")
                .contains("scope_type = 'ORGANIZATION'")
                .contains("scope_type = 'APPLICATION' AND application_id = application:`" + applicationId + "`");
    }

    @Test
    void hasPermission_returnsFalseWhenNoAssignmentMatches() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        boolean result = adapter.hasPermission(identity, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ORGANIZATION, UUID.randomUUID());

        assertThat(result).isFalse();
    }

    @Test
    void hasPermissionForEnvironment_checksAllInheritedScopesWithinEnvironmentOrganization() {
        UUID environmentId = UUID.randomUUID();
        doReturn(emptyResponse()).when(surreal).query(anyString());

        adapter.hasPermission(identity, PermissionCode.MESSAGE_CREATE,
                AuthorizationScopeType.ENVIRONMENT, environmentId);

        assertThat(capturedQuery())
                .contains("organization_id = (SELECT VALUE application_id.organization_id FROM environment:`"
                        + environmentId + "`)")
                .contains("scope_type = 'ORGANIZATION'")
                .contains("scope_type = 'APPLICATION'")
                .contains("scope_type = 'ENVIRONMENT' AND environment_id = environment:`" + environmentId + "`");
    }

    @Test
    void findAuthorizedApplicationIds_mapsIdsAcrossOrganizationsAndRestrictsEachMembership() {
        UUID applicationId = UUID.randomUUID();
        doReturn(responseWith(documentWith("authorized_id", recordIdValue("application", applicationId))))
                .when(surreal).query(anyString());

        List<UUID> result = adapter.findAuthorizedApplicationIds(
                identity, PermissionCode.CONTEXT_SELECT);

        assertThat(result).containsExactly(applicationId);
        assertThat(capturedQuery())
                .startsWith("SELECT id AS authorized_id FROM application WHERE count(")
                .contains("membership WHERE identity_id IN")
                .contains("membership WHERE identity_id IN (SELECT VALUE id FROM external_identity")
                .contains("AND membership_id.organization_id = $parent.organization_id")
                .contains("scope_type = 'ORGANIZATION' AND organization_id = $parent.organization_id")
                .contains("application_id = $parent.id")
                .contains("environment_id.application_id = $parent.id")
                .doesNotContain("active");
    }

    @Test
    void findAuthorizedEnvironmentIdsInfersAndEnforcesApplicationOrganization() {
        UUID applicationId = UUID.randomUUID();
        doReturn(emptyResponse()).when(surreal).query(anyString());

        List<UUID> result = adapter.findAuthorizedEnvironmentIds(
                identity, PermissionCode.CONTEXT_SELECT, applicationId);

        assertThat(result).isEmpty();
        assertThat(capturedQuery())
                .contains("application_id = application:`" + applicationId + "`")
                .contains("application_id.organization_id = (SELECT VALUE organization_id FROM application:`"
                        + applicationId + "`)[0]")
                .contains("membership WHERE identity_id IN")
                .contains("AND membership_id.organization_id = (SELECT VALUE organization_id FROM application:`"
                        + applicationId + "`)[0]")
                .contains("scope_type = 'APPLICATION' AND application_id = application:`" + applicationId + "`")
                .contains("scope_type = 'ENVIRONMENT' AND environment_id = $parent.id");
    }

    @Test
    void queryEscapesIdentityComponents() {
        ExternalIdentity quotedIdentity = new ExternalIdentity("is'suer", "sub\\ject", null,
                PrincipalType.SERVICE, Instant.MAX);
        doReturn(emptyResponse()).when(surreal).query(anyString());

        adapter.hasPermission(quotedIdentity, PermissionCode.CONTEXT_SELECT,
                AuthorizationScopeType.ORGANIZATION, UUID.randomUUID());

        assertThat(capturedQuery()).contains("issuer = 'is\\'suer' AND subject = 'sub\\\\ject'");
    }

    @Test
    void hasPermission_wrapsDatabaseFailureAsTechnicalBusinessException() {
        RuntimeException cause = new RuntimeException("database unavailable");
        doThrow(cause).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.hasPermission(identity, PermissionCode.MESSAGE_READ,
                AuthorizationScopeType.ORGANIZATION, UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Error al consultar autorizaciones en la base de datos SurrealDB"));
        verify(log).error("Error al consultar autorizaciones en SurrealDB", cause);
    }

    @Test
    void findAuthorizedApplicationIds_wrapsDatabaseFailureAsTechnicalBusinessException() {
        RuntimeException cause = new RuntimeException("database unavailable");
        doThrow(cause).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.findAuthorizedApplicationIds(
                identity, PermissionCode.CONTEXT_SELECT))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Error al consultar autorizaciones en la base de datos SurrealDB"));
        verify(log).error("Error al consultar autorizaciones en SurrealDB", cause);
    }

    private String capturedQuery() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(surreal).query(captor.capture());
        return captor.getValue();
    }

    private Object documentWith(final String field, final Value value) {
        Object document = mock(Object.class);
        doReturn(value).when(document).get(field);
        return document;
    }

    private Value recordIdValue(final String table, final UUID id) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn(table + ":" + id);
        Value value = mock(Value.class);
        when(value.isRecordId()).thenReturn(true);
        when(value.getRecordId()).thenReturn(recordId);
        return value;
    }

    private Response responseWith(final Object document) {
        Response response = mock(Response.class);
        Value statement = mock(Value.class);
        Array array = mock(Array.class);
        Value item = mock(Value.class);
        when(response.size()).thenReturn(1);
        when(response.take(0)).thenReturn(statement);
        when(statement.isArray()).thenReturn(true);
        when(statement.getArray()).thenReturn(array);
        when(array.len()).thenReturn(1);
        when(array.get(0)).thenReturn(item);
        when(item.isObject()).thenReturn(true);
        when(item.getObject()).thenReturn(document);
        return response;
    }

    private Response emptyResponse() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        return response;
    }
}

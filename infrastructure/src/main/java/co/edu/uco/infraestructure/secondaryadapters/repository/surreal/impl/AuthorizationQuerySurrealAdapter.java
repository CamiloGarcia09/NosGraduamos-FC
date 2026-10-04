package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PermissionCode;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import com.surrealdb.Surreal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class AuthorizationQuerySurrealAdapter extends SurrealCatalogSupport implements AuthorizationQueryPort {

    private static final String QUERY_ERROR = "Error al consultar autorizaciones en SurrealDB";
    private static final String TECHNICAL_ERROR =
            "Error al consultar autorizaciones en la base de datos SurrealDB";
    private static final String ORGANIZATION_ID_QUERY_PREFIX = "(SELECT VALUE organization_id FROM ";

    public AuthorizationQuerySurrealAdapter(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        super(surreal, loggerFactory.getLogger(AuthorizationQuerySurrealAdapter.class));
    }

    @Override
    public boolean hasPermission(final ExternalIdentity identity, final PermissionCode permission,
                                 final AuthorizationScopeType scopeType, final UUID scopeId) {
        final String sql = "SELECT true AS allowed FROM role_assignment WHERE "
                + authorizationPredicate(identity, permission, organizationExpression(scopeType, scopeId))
                + " LIMIT 1;";
        try {
            return queryOne(sql, QUERY_ERROR, row -> true).orElse(false);
        } catch (final BusinessException exception) {
            throw exception;
        } catch (final RuntimeException exception) {
            throw technicalException(exception);
        }
    }

    @Override
    public List<UUID> findAuthorizedApplicationIds(final ExternalIdentity identity,
                                                    final PermissionCode permission) {
        final String organization = "$parent.organization_id";
        final String sql = "SELECT id AS authorized_id FROM application WHERE "
                + "count((SELECT id FROM role_assignment WHERE "
                 + authorizationPredicate(identity, permission, organization) + ")) > 0;";
        return queryIds(sql);
    }

    @Override
    public List<UUID> findAuthorizedEnvironmentIds(final ExternalIdentity identity,
                                                    final PermissionCode permission,
                                                    final UUID applicationId) {
        final String application = recordIdLiteral("application", applicationId.toString());
        final String organization = ORGANIZATION_ID_QUERY_PREFIX + application + ")[0]";
        final String sql = "SELECT id AS authorized_id FROM environment WHERE application_id = "
                + application + " AND application_id.organization_id = " + organization
                + " AND count((SELECT id FROM role_assignment WHERE "
                + authorizationPredicate(identity, permission, organization) + ")) > 0;";
        return queryIds(sql);
    }

    private List<UUID> queryIds(final String sql) {
        try {
            return query(sql, QUERY_ERROR, row -> extractIdAsUUID(row.get("authorized_id")));
        } catch (final BusinessException exception) {
            throw exception;
        } catch (final RuntimeException exception) {
            throw technicalException(exception);
        }
    }

    private String authorizationPredicate(final ExternalIdentity identity, final PermissionCode permission,
                                          final String organizationExpression) {
        return "membership_id IN (SELECT VALUE id FROM membership WHERE identity_id IN "
                + "(SELECT VALUE id FROM external_identity WHERE issuer = " + quote(identity.issuer())
                + " AND subject = " + quote(identity.subject()) + "))"
                + " AND membership_id.organization_id = " + organizationExpression
                + " AND organization_id = " + organizationExpression
                + " AND role_id IN (SELECT VALUE role_id FROM role_permission WHERE permission_id IN "
                + "(SELECT VALUE id FROM permission WHERE code = " + quote(permission.name()) + "))";
    }

    private String organizationExpression(final AuthorizationScopeType scopeType, final UUID scopeId) {
        final String scope = recordIdLiteral(scopeTable(scopeType), scopeId.toString());
        return switch (scopeType) {
            case ORGANIZATION -> scope;
            case APPLICATION -> ORGANIZATION_ID_QUERY_PREFIX + scope + ")[0]";
            case ENVIRONMENT -> "(SELECT VALUE application_id.organization_id FROM " + scope + ")[0]";
        };
    }

    private String scopeTable(final AuthorizationScopeType scopeType) {
        return scopeType.name().toLowerCase();
    }

    private BusinessException technicalException(final RuntimeException exception) {
        return BusinessException.buildTechnicalException(TECHNICAL_ERROR, exception, ExceptionLocation.INFRASTRUCTURE);
    }
}

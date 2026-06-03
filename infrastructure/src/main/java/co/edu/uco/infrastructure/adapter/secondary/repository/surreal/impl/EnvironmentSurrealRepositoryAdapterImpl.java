package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.ApplicationData;
import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_SURREAL;

@Slf4j
@Repository
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_SURREAL)
public class EnvironmentSurrealRepositoryAdapterImpl implements EnvironmentRepository {

    private static final String SURREAL_TABLE_ENVIRONMENT = "environment";

    private final Surreal surreal;

    public EnvironmentSurrealRepositoryAdapterImpl(final Surreal surreal) {
        this.surreal = surreal;
    }

    @Override
    public Optional<EnvironmentData> findById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_ENVIRONMENT, id) + " LIMIT 1;";
        return findOneEnvironment(sql);
    }

    private Optional<EnvironmentData> findOneEnvironment(final String sql) {
        try {
            final Response response = surreal.query(sql);
            if (response == null || response.size() == 0) {
                return Optional.empty();
            }
            final Value statementResult = response.take(0);
            if (statementResult == null || !statementResult.isArray()) {
                return Optional.empty();
            }
            final Array array = statementResult.getArray();
            if (array.len() == 0) {
                return Optional.empty();
            }
            final Value first = array.get(0);
            if (first == null || !first.isObject()) {
                return Optional.empty();
            }
            return Optional.of(toEnvironmentData(first.getObject()));
        } catch (final RuntimeException ex) {
            log.error("Error querying SurrealDB for environment. Query: {}", sql, ex);
            throw ex;
        }
    }

    private static EnvironmentData toEnvironmentData(final Object obj) {
        final EnvironmentData data = EnvironmentData.build();
        data.setId(extractIdAsUUID(obj.get("id")));
        data.setName(stringOf(obj.get("name")));
        
        final ApplicationData app = ApplicationData.build();
        final Value appIdValue = obj.get("application_id");
        if (appIdValue != null && appIdValue.isThing()) {
            app.setId(UUID.fromString(appIdValue.getThing().getId().toString()));
        }
        data.setApplication(app);
        
        return data;
    }

    private static UUID extractIdAsUUID(final Value value) {
        if (value == null) return UUID.randomUUID();
        if (value.isThing()) {
            try {
                return UUID.fromString(value.getThing().getId().toString());
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        if (value.isString()) {
            try {
                return UUID.fromString(value.getString());
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        return UUID.randomUUID();
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toPrettyString();
    }
}

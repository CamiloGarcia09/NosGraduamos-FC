package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class EnvironmentSurrealRepositoryAdapterImpl implements EnvironmentRepository {

    private static final String SURREAL_TABLE_ENVIRONMENT = "environment";

    private final LoggingPort log;
    private final Surreal surreal;

    public EnvironmentSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(EnvironmentSurrealRepositoryAdapterImpl.class);
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
        if (appIdValue != null) {
            app.setId(extractIdAsUUID(appIdValue));
        }
        data.setApplication(app);
        
        return data;
    }

    private static UUID extractIdAsUUID(final Value value) {
        if (value == null) return UUID.randomUUID();
        if (value.isThing()) {
            try {
                final String fullId = value.getThing().toString();
                final int separator = fullId.indexOf(':');
                if (separator > 0) {
                    final String idPart = cleanThingId(fullId.substring(separator + 1));
                    return UUID.fromString(idPart);
                }
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        if (value.isString()) {
            try {
                final String raw = value.getString();
                final int separator = raw.indexOf(':');
                final String idPart = separator > 0 ? cleanThingId(raw.substring(separator + 1)) : cleanThingId(raw);
                return UUID.fromString(idPart);
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        return UUID.randomUUID();
    }

    private static String cleanThingId(String id) {
        if (id == null) return "";
        id = id.trim();
        if (id.length() >= 2 && id.startsWith("`") && id.endsWith("`")) {
            id = id.substring(1, id.length() - 1);
        }
        if (id.length() >= 2 && id.startsWith("\u27E8") && id.endsWith("\u27E9")) {
            id = id.substring(1, id.length() - 1);
        }
        final int separator = id.indexOf(':');
        if (separator > 0) {
            id = id.substring(separator + 1);
        }
        return id.trim();
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toPrettyString();
    }
}

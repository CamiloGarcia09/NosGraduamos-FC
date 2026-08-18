package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenStateSurrealRepositoryAdapter;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_NAME;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.SURREAL_TABLE_TOKEN_STATE;

@Repository
public class TokenStateSurrealRepositoryAdapterImpl implements TokenStateSurrealRepositoryAdapter {

    private static final UUID DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final LoggingPort log;
    private final Surreal surreal;

    public TokenStateSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(TokenStateSurrealRepositoryAdapterImpl.class);
        this.surreal = surreal;
    }

    @Override
    public StatusTokenSurrealModel findStatusTokenSurrealModelById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_TOKEN_STATE, id) + " LIMIT 1;";
        return findOne(sql);
    }

    @Override
    public StatusTokenSurrealModel findStatusTokenSurrealModelByName(final String name) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_TOKEN_STATE
                + " WHERE " + FIELD_NAME + " = " + quote(name) + " LIMIT 1;";
        return findOne(sql);
    }

    private StatusTokenSurrealModel findOne(final String sql) {
        final Response response = surreal.query(sql);
        if (response == null || response.size() == 0) {
            return StatusTokenSurrealModel.build();
        }
        final Value statementResult = response.take(0);
        if (statementResult == null || !statementResult.isArray()) {
            return StatusTokenSurrealModel.build();
        }
        final Array array = statementResult.getArray();
        if (array.len() == 0) {
            return StatusTokenSurrealModel.build();
        }
        final Value first = array.get(0);
        if (first == null || !first.isObject()) {
            return StatusTokenSurrealModel.build();
        }
        final Object obj = first.getObject();
        return new StatusTokenSurrealModel(extractUuid(obj.get("id")), stringOf(obj.get(FIELD_NAME)));
    }

    private UUID extractUuid(final Value value) {
        if (value == null || value.isNull() || value.isNone()) {
            return DEFAULT_UUID;
        }
        try {
            String rawId = null;
            if (value.isRecordId()) {
                rawId = cleanIdPart(value.getRecordId().toString());
            } else if (value.isUuid()) {
                return value.getUuid();
            } else if (value.isString()) {
                rawId = cleanIdPart(value.getString());
            } else {
                rawId = cleanIdPart(value.toString());
            }
            if (rawId == null || rawId.isBlank()) {
                return DEFAULT_UUID;
            }
            return UUID.fromString(rawId);
        } catch (final RuntimeException ex) {
            return DEFAULT_UUID;
        }
    }

    private static String cleanIdPart(String id) {
        if (id == null) return "";
        id = id.trim();
        final int separator = id.indexOf(':');
        if (separator > 0) {
            id = id.substring(separator + 1);
        }
        id = id.trim();
        if (id.length() >= 2 && id.startsWith("`") && id.endsWith("`")) {
            id = id.substring(1, id.length() - 1);
        }
        if (id.length() >= 2 && id.startsWith("\u27E8") && id.endsWith("\u27E9")) {
            id = id.substring(1, id.length() - 1);
        }
        return id.trim();
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toString();
    }
}

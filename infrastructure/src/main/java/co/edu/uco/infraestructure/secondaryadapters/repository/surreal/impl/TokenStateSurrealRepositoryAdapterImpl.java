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
            if (value.isThing()) return UUID.fromString(value.getThing().getId().toString());
            if (value.isUuid()) return value.getUuid();
            if (value.isString()) return UUID.fromString(value.getString());
            return UUID.fromString(value.toPrettyString());
        } catch (final RuntimeException ex) {
            log.warn("Cannot parse UUID from value: {}", value.toPrettyString(), ex);
            return DEFAULT_UUID;
        }
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toPrettyString();
    }
}

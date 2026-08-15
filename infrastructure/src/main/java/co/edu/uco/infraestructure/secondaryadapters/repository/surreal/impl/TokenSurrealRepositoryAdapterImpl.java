package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenSurrealRepositoryAdapter;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.datetime;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_CREATION_DATE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_ENVIRONMENT_ID;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_EXPIRATION_DATE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_SECRET_NAME;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_STATE_ID;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.SURREAL_TABLE_TOKEN;

@Repository
public class TokenSurrealRepositoryAdapterImpl implements TokenSurrealRepositoryAdapter {

    private final LoggingPort log;
    private final Surreal surreal;

    public TokenSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(TokenSurrealRepositoryAdapterImpl.class);
        this.surreal = surreal;
    }

    @Override
    public TokenSurrealModel upsert(final TokenSurrealModel model) {
        final String sql = "UPSERT " + recordIdLiteral(SURREAL_TABLE_TOKEN, model.getId())
                + " CONTENT { "
                + FIELD_SECRET_NAME     + ": " + quote(model.getSecretName())          + ", "
                + FIELD_CREATION_DATE   + ": " + datetime(model.getCreationDate())     + ", "
                + FIELD_EXPIRATION_DATE + ": " + datetime(model.getExpirationDate())   + ", "
                + FIELD_ENVIRONMENT_ID  + ": " + quote(model.getEnvironmentId())       + ", "
                + FIELD_STATE_ID        + ": " + quote(model.getStateId())
                + " } RETURN AFTER;";

        try {
            surreal.query(sql);
            log.debug("Token persisted into SurrealDB: {}", model.getId());
            return model;
        } catch (final RuntimeException ex) {
            log.error("Error saving token in SurrealDB. Query: {}", sql, ex);
            throw ex;
        }
    }

    @Override
    public Optional<TokenSurrealModel> findTokenSurrealModelById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_TOKEN, id) + " LIMIT 1;";
        return findOne(sql);
    }

    @Override
    public Optional<TokenSurrealModel> findTokenSurrealModelByEnvironmentIdAndStateId(final String environmentId,
                                                                                       final String stateId) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_TOKEN
                + " WHERE " + FIELD_ENVIRONMENT_ID + " = " + quote(environmentId)
                + " AND "  + FIELD_STATE_ID       + " = " + quote(stateId)
                + " LIMIT 1;";
        return findOne(sql);
    }

    private Optional<TokenSurrealModel> findOne(final String sql) {
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
        return Optional.of(toModel(first.getObject()));
    }

    private static TokenSurrealModel toModel(final Object obj) {
        return new TokenSurrealModel(
                extractIdAsString(obj.get("id")),
                stringOf(obj.get(FIELD_SECRET_NAME)),
                dateOf(obj.get(FIELD_CREATION_DATE)),
                dateOf(obj.get(FIELD_EXPIRATION_DATE)),
                stringOf(obj.get(FIELD_ENVIRONMENT_ID)),
                stringOf(obj.get(FIELD_STATE_ID))
        );
    }

    private static String extractIdAsString(final Value value) {
        if (value == null) return "";
        if (value.isThing()) return cleanIdPart(value.getThing().getId().toString());
        if (value.isString()) return value.getString();
        return value.toPrettyString();
    }

    private static String cleanIdPart(String id) {
        if (id == null) return "";
        id = id.trim();
        if (id.length() >= 2 && id.startsWith("`") && id.endsWith("`")) {
            id = id.substring(1, id.length() - 1);
        }
        if (id.length() >= 2 && id.startsWith("\u27E8") && id.endsWith("\u27E9")) {
            id = id.substring(1, id.length() - 1);
        }
        return id;
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toPrettyString();
    }

    private static LocalDateTime dateOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return LocalDateTime.now();
        if (value.isDateTime()) return value.getDateTime().toLocalDateTime();
        return LocalDateTime.now();
    }
}

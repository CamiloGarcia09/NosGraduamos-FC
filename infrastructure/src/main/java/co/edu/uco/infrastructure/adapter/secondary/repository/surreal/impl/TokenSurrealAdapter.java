package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.utils.exception.BusinessException;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.datetime;
import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_CREATION_DATE;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_ENVIRONMENT_ID;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_EXPIRATION_DATE;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_SECRET_NAME;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_STATE_ID;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_SURREAL;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.SURREAL_TABLE_TOKEN;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.TOKEN_SURREAL_ADAPTER;

/**
 * SurrealDB adapter for the Token aggregate.
 * <p>
 * Active when {@code persistence.primary=surreal}. Replaces both
 * {@code TokenPostgresSQLAdapter} (write) and {@code TokenMongoAdapter} (read).
 * <p>
 * Uses {@code query(String)} with literal values because the native binding
 * for {@code queryBind} is missing in the SurrealDB Java SDK 0.2.1.
 */
@Slf4j
@Primary
@Component(TOKEN_SURREAL_ADAPTER)
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_SURREAL)
public final class TokenSurrealAdapter implements TokenRepository, FindTokenRepository {

    private final Surreal surreal;

    public TokenSurrealAdapter(final Surreal surreal) {
        this.surreal = surreal;
    }

    @Override
    public TokenData save(final TokenData tokenData) {
        final String sql = "UPSERT " + recordIdLiteral(SURREAL_TABLE_TOKEN, tokenData.getId())
                + " CONTENT { "
                + FIELD_SECRET_NAME     + ": " + quote(tokenData.getSecretName())          + ", "
                + FIELD_CREATION_DATE   + ": " + datetime(tokenData.getCreationDate())     + ", "
                + FIELD_EXPIRATION_DATE + ": " + datetime(tokenData.getExpirationDate())   + ", "
                + FIELD_ENVIRONMENT_ID  + ": " + quote(tokenData.getEnvironmentId())       + ", "
                + FIELD_STATE_ID        + ": " + quote(tokenData.getStateId())
                + " } RETURN AFTER;";

        try {
            surreal.query(sql);
            log.debug("Token persisted into SurrealDB: {}", tokenData.getId());
            return tokenData;
        } catch (final RuntimeException ex) {
            log.error("Error saving token in SurrealDB. Query: {}", sql, ex);
            throw ex;
        }
    }

    @Override
    public TokenData findById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_TOKEN, id) + " LIMIT 1;";
        return findOne(sql).orElseThrow(() ->
                BusinessException.buildUserException("Token not found in SurrealDB: " + id));
    }

    @Override
    public Optional<TokenData> findByEnvironmentAndState(final String environment, final String state) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_TOKEN
                + " WHERE " + FIELD_ENVIRONMENT_ID + " = " + quote(environment)
                + " AND "  + FIELD_STATE_ID       + " = " + quote(state)
                + " LIMIT 1;";
        return findOne(sql);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Optional<TokenData> findOne(final String sql) {
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
        return Optional.of(toTokenData(first.getObject()));
    }

    private static TokenData toTokenData(final Object obj) {
        final TokenData token = new TokenData();
        token.setId(extractIdAsString(obj.get("id")));
        token.setSecretName(stringOf(obj.get(FIELD_SECRET_NAME)));
        token.setCreationDate(dateOf(obj.get(FIELD_CREATION_DATE)));
        token.setExpirationDate(dateOf(obj.get(FIELD_EXPIRATION_DATE)));
        token.setEnvironmentId(stringOf(obj.get(FIELD_ENVIRONMENT_ID)));
        token.setStateId(stringOf(obj.get(FIELD_STATE_ID)));
        return token;
    }

    private static String extractIdAsString(final Value value) {
        if (value == null) return "";
        if (value.isThing()) {
            return value.getThing().getId().toString();
        }
        if (value.isString()) {
            return value.getString();
        }
        return value.toPrettyString();
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid())   return value.getUuid().toString();
        return value.toPrettyString();
    }

    private static LocalDateTime dateOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) {
            return LocalDateTime.now();
        }
        if (value.isDateTime()) {
            return value.getDateTime().toLocalDateTime();
        }
        return LocalDateTime.now();
    }
}

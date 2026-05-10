package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.utils.exception.BusinessException;
import com.surrealdb.RecordId;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
 * Replaces both {@code TokenPostgresSQLAdapter} (write) and
 * {@code TokenMongoAdapter} (read) when {@code persistence.primary=surreal}.
 * Persists tokens into the {@code token} table inside the configured
 * SurrealDB namespace/database via the {@link Surreal} bean.
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
        final RecordId rid = new RecordId(SURREAL_TABLE_TOKEN, tokenData.getId());

        final Map<String, Object> content = new LinkedHashMap<>();
        content.put(FIELD_SECRET_NAME, tokenData.getSecretName());
        content.put(FIELD_CREATION_DATE, toInstant(tokenData.getCreationDate()));
        content.put(FIELD_EXPIRATION_DATE, toInstant(tokenData.getExpirationDate()));
        content.put(FIELD_ENVIRONMENT_ID, tokenData.getEnvironmentId());
        content.put(FIELD_STATE_ID, tokenData.getStateId());

        final Map<String, Object> params = new LinkedHashMap<>();
        params.put("rid", rid);
        params.put("data", content);

        try {
            surreal.queryBind("UPSERT $rid CONTENT $data RETURN AFTER;", params);
            log.debug("Token persisted into SurrealDB: {}", tokenData.getId());
            return tokenData;
        } catch (final RuntimeException ex) {
            log.error("Error saving token in SurrealDB", ex);
            throw ex;
        }
    }

    @Override
    public TokenData findById(final String id) {
        return findOne("SELECT * FROM $rid LIMIT 1;",
                Map.of("rid", new RecordId(SURREAL_TABLE_TOKEN, id)))
                .orElseThrow(() -> BusinessException.buildUserException(
                        "Token not found in SurrealDB: " + id));
    }

    @Override
    public Optional<TokenData> findByEnvironmentAndState(final String environment, final String state) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_TOKEN
                + " WHERE " + FIELD_ENVIRONMENT_ID + " = $env"
                + " AND "  + FIELD_STATE_ID       + " = $st"
                + " LIMIT 1;";
        return findOne(sql, Map.of("env", environment, "st", state));
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Optional<TokenData> findOne(final String sql, final Map<String, Object> params) {
        final Value statementResult = surreal.queryBind(sql, params).take(0);
        if (statementResult == null || !statementResult.isArray()) {
            return Optional.empty();
        }
        final var array = statementResult.getArray();
        if (array.len() == 0) {
            return Optional.empty();
        }
        final var first = array.get(0);
        if (first == null || !first.isObject()) {
            return Optional.empty();
        }
        return Optional.of(toTokenData(first.getObject()));
    }

    private TokenData toTokenData(final com.surrealdb.Object obj) {
        final TokenData token = new TokenData();
        token.setId(extractId(obj.get("id")));
        token.setSecretName(stringOf(obj.get(FIELD_SECRET_NAME)));
        token.setCreationDate(dateOf(obj.get(FIELD_CREATION_DATE)));
        token.setExpirationDate(dateOf(obj.get(FIELD_EXPIRATION_DATE)));
        token.setEnvironmentId(stringOf(obj.get(FIELD_ENVIRONMENT_ID)));
        token.setStateId(stringOf(obj.get(FIELD_STATE_ID)));
        return token;
    }

    private static String extractId(final Value value) {
        if (value == null) return "";
        if (value.isThing()) {
            // Thing.toString() yields "table:id"; we keep only the id part.
            final String s = value.getThing().toString();
            final int idx = s.indexOf(':');
            return idx >= 0 ? s.substring(idx + 1) : s;
        }
        return stringOf(value);
    }

    private static String stringOf(final Value value) {
        if (value == null) return "";
        return value.toString();
    }

    private static LocalDateTime dateOf(final Value value) {
        if (value == null) return LocalDateTime.now();
        try {
            return LocalDateTime.ofInstant(Instant.parse(value.toString()), ZoneOffset.UTC);
        } catch (final RuntimeException ignored) {
            return LocalDateTime.now();
        }
    }

    private static Instant toInstant(final LocalDateTime ldt) {
        return ldt == null ? Instant.now() : ldt.toInstant(ZoneOffset.UTC);
    }
}

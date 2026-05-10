package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.StatusTokenData;
import co.edu.uco.core.domain.port.out.repository.token.TokenStateRepository;
import com.surrealdb.RecordId;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_NAME;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_SURREAL;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.SURREAL_TABLE_TOKEN_STATE;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.TOKEN_STATE_SURREAL_ADAPTER;

/**
 * SurrealDB adapter for the TokenState catalog.
 * <p>
 * Active when {@code persistence.primary=surreal}. Reads token states
 * from the {@code token_state} table seeded by surreal-init.surql.
 */
@Slf4j
@Primary
@Component(TOKEN_STATE_SURREAL_ADAPTER)
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_SURREAL)
public final class TokenStateSurrealAdapter implements TokenStateRepository {

    private final Surreal surreal;

    public TokenStateSurrealAdapter(final Surreal surreal) {
        this.surreal = surreal;
    }

    @Override
    public StatusTokenData findByStatus(final String id) {
        final Value result = surreal.queryBind(
                "SELECT * FROM $rid LIMIT 1;",
                Map.of("rid", new RecordId(SURREAL_TABLE_TOKEN_STATE, id))
        ).take(0);
        return toStatus(result);
    }

    @Override
    public StatusTokenData findByStatusName(final String name) {
        final Value result = surreal.queryBind(
                "SELECT * FROM " + SURREAL_TABLE_TOKEN_STATE + " WHERE " + FIELD_NAME + " = $name LIMIT 1;",
                Map.of("name", name)
        ).take(0);
        return toStatus(result);
    }

    private static StatusTokenData toStatus(final Value statementResult) {
        if (statementResult == null || !statementResult.isArray()) {
            return StatusTokenData.build();
        }
        final var array = statementResult.getArray();
        if (array.len() == 0) {
            return StatusTokenData.build();
        }
        final var first = array.get(0);
        if (first == null || !first.isObject()) {
            return StatusTokenData.build();
        }
        final com.surrealdb.Object obj = first.getObject();
        return new StatusTokenData(extractUuid(obj.get("id")), valueAsString(obj.get(FIELD_NAME)));
    }

    private static UUID extractUuid(final Value value) {
        if (value == null) {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        try {
            if (value.isThing()) {
                final String s = value.getThing().toString();
                final int idx = s.indexOf(':');
                return UUID.fromString(idx >= 0 ? s.substring(idx + 1) : s);
            }
            return UUID.fromString(value.toString());
        } catch (final RuntimeException ex) {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }

    private static String valueAsString(final Value value) {
        return value == null ? "" : value.toString();
    }
}

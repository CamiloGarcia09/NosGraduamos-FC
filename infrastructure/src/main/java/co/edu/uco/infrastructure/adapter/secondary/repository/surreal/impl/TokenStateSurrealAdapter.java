package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.StatusTokenData;
import co.edu.uco.core.domain.port.out.repository.token.TokenStateRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.TokenStateSurrealMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.TokenStateSurrealRepositoryAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_SURREAL;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.TOKEN_STATE_SURREAL_ADAPTER;

/**
 * SurrealDB adapter for the TokenState catalog.
 * Active when {@code persistence.primary=surreal}.
 */
@Primary
@Component(TOKEN_STATE_SURREAL_ADAPTER)
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_SURREAL)
public final class TokenStateSurrealAdapter implements TokenStateRepository {

    private final TokenStateSurrealRepositoryAdapter tokenStateSurrealRepositoryAdapter;
    private final TokenStateSurrealMapper mapper;

    public TokenStateSurrealAdapter(final TokenStateSurrealRepositoryAdapter tokenStateSurrealRepositoryAdapter,
                                    final TokenStateSurrealMapper mapper) {
        this.tokenStateSurrealRepositoryAdapter = tokenStateSurrealRepositoryAdapter;
        this.mapper = mapper;
    }

    @Override
    public StatusTokenData findByStatus(final String id) {
        return mapper.mapperData(tokenStateSurrealRepositoryAdapter.findStatusTokenSurrealModelById(id));
    }

    @Override
    public StatusTokenData findByStatusName(final String name) {
        return mapper.mapperData(tokenStateSurrealRepositoryAdapter.findStatusTokenSurrealModelByName(name));
    }
}

package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.TokenStateSurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenStateSurrealRepositoryAdapter;
import org.springframework.stereotype.Component;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.TOKEN_STATE_SURREAL_ADAPTER;

@Component(TOKEN_STATE_SURREAL_ADAPTER)
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

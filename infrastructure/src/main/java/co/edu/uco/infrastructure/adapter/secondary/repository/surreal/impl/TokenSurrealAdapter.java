package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.TokenSurrealMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.TokenSurrealRepositoryAdapter;
import co.edu.uco.utils.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.TOKEN_SURREAL_ADAPTER;

@Component(TOKEN_SURREAL_ADAPTER)
public final class TokenSurrealAdapter implements TokenRepository, FindTokenRepository {

    private final TokenSurrealRepositoryAdapter tokenSurrealRepositoryAdapter;
    private final TokenSurrealMapper mapper;

    public TokenSurrealAdapter(final TokenSurrealRepositoryAdapter tokenSurrealRepositoryAdapter,
                               final TokenSurrealMapper mapper) {
        this.tokenSurrealRepositoryAdapter = tokenSurrealRepositoryAdapter;
        this.mapper = mapper;
    }

    @Override
    public TokenData save(final TokenData tokenData) {
        tokenSurrealRepositoryAdapter.upsert(mapper.mapperModel(tokenData));
        return tokenData;
    }

    @Override
    public TokenData findById(final String id) {
        return tokenSurrealRepositoryAdapter.findTokenSurrealModelById(id)
                .map(mapper::mapperData)
                .orElseThrow(() -> BusinessException.buildUserException("Token not found in SurrealDB: " + id));
    }

    @Override
    public Optional<TokenData> findByEnvironmentAndState(final String environment, final String state) {
        return tokenSurrealRepositoryAdapter.findTokenSurrealModelByEnvironmentIdAndStateId(environment, state)
                .map(mapper::mapperData);
    }
}

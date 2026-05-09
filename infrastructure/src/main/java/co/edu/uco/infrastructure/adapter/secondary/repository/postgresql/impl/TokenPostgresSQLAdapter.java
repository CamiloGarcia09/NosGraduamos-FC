package co.edu.uco.infrastructure.adapter.secondary.repository.postgresql.impl;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.TokenDataMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.postgresql.TokenPostgresSQLRepositoryAdapter;
import org.springframework.stereotype.Component;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.POSTGRESQL_ADAPTER;

@Component(POSTGRESQL_ADAPTER)
public final class TokenPostgresSQLAdapter implements TokenRepository {
    private final TokenPostgresSQLRepositoryAdapter tokenPostgresSQLRepositoryAdapter;
    private final TokenDataMapper tokenDataMapper;
    public TokenPostgresSQLAdapter(TokenPostgresSQLRepositoryAdapter tokenPostgresSQLRepositoryAdapter, TokenDataMapper tokenDataMapper) {
        this.tokenPostgresSQLRepositoryAdapter = tokenPostgresSQLRepositoryAdapter;
        this.tokenDataMapper = tokenDataMapper;
    }
    @Override
    public TokenData save(TokenData tokenData) {
        return tokenDataMapper.mapperData(
                tokenPostgresSQLRepositoryAdapter.save(
                        tokenDataMapper.mapperModel(tokenData)
                )
        );
    }
}
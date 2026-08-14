package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenRepository;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.TokenSurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenSurrealRepositoryAdapter;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.TOKEN_SURREAL_ADAPTER;

@Component(TOKEN_SURREAL_ADAPTER)
public final class TokenSurrealAdapter implements TokenRepository, FindTokenRepository {

    private final TokenSurrealRepositoryAdapter tokenSurrealRepositoryAdapter;
    private final TokenSurrealMapper mapper;
    private final CatalogPort catalogPort;

    public TokenSurrealAdapter(final TokenSurrealRepositoryAdapter tokenSurrealRepositoryAdapter,
                               final TokenSurrealMapper mapper, CatalogPort catalogPort) {
        this.tokenSurrealRepositoryAdapter = tokenSurrealRepositoryAdapter;
        this.mapper = mapper;
        this.catalogPort = catalogPort;
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
                .orElseThrow(() -> BusinessException.buildUserException(catalogPort.getMessage("FUN_049") + id));
    }

    @Override
    public Optional<TokenData> findByEnvironmentAndState(final String environment, final String state) {
        return tokenSurrealRepositoryAdapter.findTokenSurrealModelByEnvironmentIdAndStateId(environment, state)
                .map(mapper::mapperData);
    }
}

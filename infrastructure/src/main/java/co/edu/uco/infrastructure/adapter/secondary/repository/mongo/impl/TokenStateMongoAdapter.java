package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.impl;

import co.edu.uco.core.domain.data.StatusTokenData;
import co.edu.uco.core.domain.port.out.repository.token.TokenStateRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.TokenStateDataMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.TokenStateMongoRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public final class TokenStateMongoAdapter implements TokenStateRepository {
    private final TokenStateMongoRepositoryAdapter tokenStateMongoRepositoryAdapter;
    private final TokenStateDataMapper mapper;
    public TokenStateMongoAdapter(TokenStateMongoRepositoryAdapter tokenStateMongoRepositoryAdapter, TokenStateDataMapper mapper) {
        this.tokenStateMongoRepositoryAdapter = tokenStateMongoRepositoryAdapter;
        this.mapper = mapper;
    }
    @Override
    public StatusTokenData findByStatus(String id) {
        return mapper.mapperData(tokenStateMongoRepositoryAdapter.findStatusTokenDocumentById(id));
    }
    @Override
    public StatusTokenData findByStatusName(String name) {
        return mapper.mapperData(tokenStateMongoRepositoryAdapter.findStatusTokenDocumentByName(name));
    }
}
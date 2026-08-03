package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.impl;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.TokenDocumentMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.TokenMongoRepositoryAdapter;
import co.edu.uco.utils.exception.BusinessException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_MONGO;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;

@Component
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_MONGO, matchIfMissing = true)
public final class TokenMongoAdapter implements FindTokenRepository {
    private final TokenMongoRepositoryAdapter tokenMongoRepositoryAdapter;
    private final TokenDocumentMapper mapper;
    public TokenMongoAdapter(TokenMongoRepositoryAdapter tokenMongoRepositoryAdapter, TokenDocumentMapper mapper) {
        this.tokenMongoRepositoryAdapter = tokenMongoRepositoryAdapter;
        this.mapper = mapper;
    }
    @Override
    public TokenData findById(String id) {
        return tokenMongoRepositoryAdapter.findTokenDocumentById(id).map(mapper::mapperData)
                .orElseThrow(() -> BusinessException.buildUserException(DetailMessageEnum.FUN_026.getContent()));
    }
    @Override
    public Optional<TokenData> findByEnvironmentAndState(String environment, String state) {
        return tokenMongoRepositoryAdapter.findTokenDocumentByEnvironmentIdAndStateId(environment,state)
                .map(mapper::mapperData);
    }
}
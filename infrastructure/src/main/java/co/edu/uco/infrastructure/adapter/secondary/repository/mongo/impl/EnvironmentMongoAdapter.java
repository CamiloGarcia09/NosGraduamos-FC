package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.impl;

import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.EnvironmentDataMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.EnvironmentRepositoryAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_MONGO;
import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PERSISTENCE_PRIMARY_PROPERTY;

@Component
@ConditionalOnProperty(name = PERSISTENCE_PRIMARY_PROPERTY, havingValue = PERSISTENCE_PRIMARY_MONGO, matchIfMissing = true)
public final class EnvironmentMongoAdapter implements EnvironmentRepository {
    private final EnvironmentRepositoryAdapter environmentRepositoryAdapter;
    private final EnvironmentDataMapper mapper;
    public EnvironmentMongoAdapter(EnvironmentRepositoryAdapter environmentRepositoryAdapter, EnvironmentDataMapper mapper) {
        this.environmentRepositoryAdapter = environmentRepositoryAdapter;
        this.mapper = mapper;
    }
    @Override
    public Optional<EnvironmentData> findById(String id) {
        return environmentRepositoryAdapter.findById(id).map(mapper::mapperData);
    }
}

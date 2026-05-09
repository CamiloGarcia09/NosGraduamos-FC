package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.impl;

import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.core.domain.port.out.repository.EnvironmentRepository;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.EnvironmentDataMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.EnvironmentRepositoryAdapter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
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
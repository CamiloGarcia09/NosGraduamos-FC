package co.edu.uco.infraestructure.secondaryadapters.repository.redis.impl;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.repository.CacheMessageRepository;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.DataMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.redis.MessageRedis;
import co.edu.uco.infraestructure.secondaryadapters.repository.redis.RedisRepositoryAdapter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.CACHE_REDIS_ADAPTER;

@Component(CACHE_REDIS_ADAPTER)
public final class MessageRedisAdapter implements CacheMessageRepository {
    private final RedisRepositoryAdapter repository;
    private final DataMapper<MessageData, MessageRedis> mapper;
    public MessageRedisAdapter(RedisRepositoryAdapter repository, DataMapper<MessageData, MessageRedis> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    public void save(MessageData data) {
        repository.save(mapper.mapperModel(data));
    }
    @Override
    public void saveWithEnvironment(MessageData data, String environmentId) {
        var messageRedis = mapper.mapperModel(data);
        messageRedis.setEnvironmentId(environmentId);
        repository.save(messageRedis);
    }
    @Override
    public Optional<MessageData> findById(UUID id) {
        return repository.findById(id).map(mapper::mapperData);
    }
    @Override
    public SimplePage<MessageData> findMessagesByEnvironment(String environment, Pageable pageable) {
        return SimplePage.of(repository.findByEnvironmentId(environment, pageable)
                .map(mapper::mapperData));
    }
    @Override
    public Optional<MessageData> findMessageByCodeAndEnvironment(String code, String environmentId) {
        return repository.findByCodeAndEnvironmentId(code, environmentId)
                .map(mapper::mapperData);

    }




}
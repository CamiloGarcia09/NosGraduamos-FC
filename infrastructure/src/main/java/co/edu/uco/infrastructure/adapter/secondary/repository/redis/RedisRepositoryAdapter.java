package co.edu.uco.infrastructure.adapter.secondary.repository.redis;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RedisRepositoryAdapter extends CrudRepository<MessageRedis, UUID> {
    Page<MessageRedis> findByEnvironmentId(String environmentId, Pageable pageable);
    Optional<MessageRedis> findByCodeAndEnvironmentId(String code, String environmentId);
    Optional<MessageRedis> findByCode(String code);
}
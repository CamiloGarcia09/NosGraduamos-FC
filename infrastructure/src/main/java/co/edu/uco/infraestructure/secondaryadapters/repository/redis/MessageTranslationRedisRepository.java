package co.edu.uco.infraestructure.secondaryadapters.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTranslationRedisRepository extends CrudRepository<MessageTranslationRedis, String> {
}
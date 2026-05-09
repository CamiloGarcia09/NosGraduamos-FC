package co.edu.uco.core.domain.port.out.repository;

import co.edu.uco.core.domain.data.MessageData;

import java.util.Optional;

public interface CacheMessageRepository extends MessageRepository {
    void saveWithEnvironment(MessageData data, String environmentId);
    Optional<MessageData> findMessageByCodeAndEnvironment(String code, String environmentId);
}
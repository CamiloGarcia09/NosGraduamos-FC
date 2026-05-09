package co.edu.uco.core.domain.port.out.repository;

import co.edu.uco.core.domain.data.MessageData;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    void save(MessageData data);
    Optional<MessageData> findById(UUID id);
    SimplePage<MessageData> findMessagesByEnvironment(String id, Pageable pageable);
    Optional<MessageData> findMessageByCodeAndEnvironment(String code, String environmentId);
}
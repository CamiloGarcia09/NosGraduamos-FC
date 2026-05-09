package co.edu.uco.core.domain.port.out.repository;

import co.edu.uco.core.domain.data.MessageData;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DataBaseMessageRepository {
    Optional<MessageData> findById(String id);
    SimplePage<MessageData> findMessagesByEnvironment(String id, Pageable pageable);
    Optional<MessageData> findMessageByCodeAndEnvironment(String code, String environmentId);
}
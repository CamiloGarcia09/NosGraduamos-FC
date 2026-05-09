package co.edu.uco.infrastructure.adapter.secondary.repository.mongo;

import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.MessageEnvironmentDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvironmentMongoRepositoryAdapter extends MongoRepository<MessageEnvironmentDocument, String> {
    Page<MessageEnvironmentDocument> findMessageEnvironmentDocumentByEnvironmentId(String environmentId,
            Pageable pageable);
    Optional<MessageEnvironmentDocument> findByEnvironmentIdAndMessageCode(String environmentId, String code);
    Optional<MessageEnvironmentDocument> findByMessageId(String messageId);
}
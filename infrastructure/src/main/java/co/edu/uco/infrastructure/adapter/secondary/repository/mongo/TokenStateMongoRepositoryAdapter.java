package co.edu.uco.infrastructure.adapter.secondary.repository.mongo;

import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.StatusTokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenStateMongoRepositoryAdapter extends MongoRepository<StatusTokenDocument, String> {
    StatusTokenDocument findStatusTokenDocumentById(String id);
    StatusTokenDocument findStatusTokenDocumentByName(String name);
}
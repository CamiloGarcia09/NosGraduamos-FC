package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.impl;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.infrastructure.adapter.secondary.repository.data.DataMapper;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.EnvironmentMongoRepositoryAdapter;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.MessageEnvironmentDocument;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.MessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.DATABASE_MONGO_ADAPTER;

@Component(DATABASE_MONGO_ADAPTER)
public final class MessageMongoAdapter implements DataBaseMessageRepository {
    private final EnvironmentMongoRepositoryAdapter environmentRepository;
    private final DataMapper<MessageData, MessageDocument> mapper;
    public MessageMongoAdapter(
            EnvironmentMongoRepositoryAdapter environmentRepository,
            DataMapper<MessageData, MessageDocument> mapper) {
        this.environmentRepository = environmentRepository;
        this.mapper = mapper;
    }
    @Override
    public Optional<MessageData> findById(String id) {
        return environmentRepository.findByMessageId(id)
                .map(doc -> mapper.mapperData(doc.getMessage()));
    }
    @Override
    public SimplePage<MessageData> findMessagesByEnvironment(String id, Pageable pageable) {
        Page<MessageEnvironmentDocument> query = environmentRepository.findMessageEnvironmentDocumentByEnvironmentId(id,
                pageable);
        Page<MessageData> messageDataPage = query.map(doc -> mapper.mapperData(doc.getMessage()));
        return SimplePage.of(messageDataPage);
    }
    @Override
    public Optional<MessageData> findMessageByCodeAndEnvironment(String code, String environmentId) {
        return environmentRepository.findByEnvironmentIdAndMessageCode(environmentId, code)
                .map(doc -> mapper.mapperData(doc.getMessage()));
    }
}
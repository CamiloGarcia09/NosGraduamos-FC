package co.edu.uco.core.application.catalog.strategy.cache;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.CacheMessageRepository;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.port.out.repository.token.PageBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilUUID.getUUIDFromString;

@Component
public final class CacheMessageCatalog extends CacheCatalog {
    private final CacheMessageRepository repository;
    public CacheMessageCatalog(CacheMessageRepository repository) {
        this.repository = repository;
    }
    @Override
    public Optional<MessageData> getMessageById(String id) {
        return repository.findById(getUUIDFromString(id));
    }
    @Override
    public String getContent(String code) {
        return repository.findById(getUUIDFromString(code)).map(MessageData::getContent).orElse(EMPTY);
    }
    @Override
    public void addMessage(MessageData messageModel) {
        repository.save(messageModel);
    }
    @Override
    public void addMessageWithEnvironment(MessageData messageModel, String environmentId) {
        repository.saveWithEnvironment(messageModel, environmentId);
    }
    @Override
    public boolean isExist(String key) {
        return getMessageById(key).isPresent();
    }
    @Override
    public SimplePage<MessageData> getMessageWithEnvironment(String environment, SimplePageRequest request) {
        var result = PageBuilder.createPageRequest(request);
        return repository.findMessagesByEnvironment(environment, result);
    }
    @Override
    public Optional<MessageData> getMessageByCodeAndEnvironment(String code, String environmentId) {
        return repository.findMessageByCodeAndEnvironment(code, environmentId);
    }
}
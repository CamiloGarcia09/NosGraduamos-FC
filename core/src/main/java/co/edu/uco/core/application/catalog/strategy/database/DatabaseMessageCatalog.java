package co.edu.uco.core.application.catalog.strategy.database;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.port.out.repository.token.PageBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.utils.helper.UtilText.EMPTY;

@Component
public final class DatabaseMessageCatalog extends DatabaseCatalog {
    private final DataBaseMessageRepository repository;
    public DatabaseMessageCatalog(DataBaseMessageRepository repository) {
        this.repository = repository;
    }
    @Override
    public Optional<MessageData> getMessageById(String code) {
        return repository.findById(code);
    }
    @Override
    public String getContent(String code) {
        return repository.findById(code).map(MessageData::getContent).orElse(EMPTY);
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
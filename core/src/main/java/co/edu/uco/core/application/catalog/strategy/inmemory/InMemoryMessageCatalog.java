package co.edu.uco.core.application.catalog.strategy.inmemory;

import co.edu.uco.core.application.catalog.MessageModel;
import co.edu.uco.core.application.catalog.InMemoryCatalogStaticRef;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageCategoryEnum;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageTypeEnum;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.helper.UtilObject;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

@Component
public final class InMemoryMessageCatalog extends InMemoryCatalog {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMessageCatalog.class);

    private Map<MessageKeyEnum, MessageModel> messages;

    @Autowired(required = false)
    private DataBaseMessageRepository dataBaseMessageRepository;

    @Override
    public MessageModel getMessageById(MessageKeyEnum code) {
        if (isNullObject(code)) {
            throw CrossWordsException.build("Message code cannot be null");
        }
        return messages.get(code);
    }

    @Override
    public String getContent(String code) {
        if (isNullObject(code)) {
            throw CrossWordsException.build("Message code cannot be null");
        }
        var key = MessageKeyEnum.of(code);
        var model = messages.get(key);
        return model != null ? model.content() : "";
    }

    @Override
    public void addMessage(MessageKeyEnum key, MessageModel messageModel) {
        messages.put(key, messageModel);
    }

    @Override
    public boolean isExist(MessageKeyEnum key) {
        return messages.containsKey(key);
    }

    @Override
    @PostConstruct
    public void loadCatalog() {
        messages = UtilObject.getDefaultIsNullObject(messages, new HashMap<>());
        tryLoadAllFromDatabase();
        InMemoryCatalogStaticRef.set(this);
    }

    @Override
    public void reloadCatalog() {
        messages.clear();
        loadCatalog();
    }

    private void tryLoadAllFromDatabase() {
        if (dataBaseMessageRepository == null) {
            log.debug("DataBaseMessageRepository not available - catalog will be empty until DB is ready");
            return;
        }
        int loaded = 0;
        int failed = 0;
        for (var key : MessageKeyEnum.values()) {
            try {
                var optional = dataBaseMessageRepository.findByCode(key.getKey());
                if (optional.isPresent()) {
                    messages.put(key, toMessageModel(key, optional.get()));
                    loaded++;
                }
            } catch (Exception e) {
                failed++;
            }
        }
        if (loaded > 0) {
            log.info("Loaded {} messages from SurrealDB into catalog ({} keys not found in DB)", loaded, failed);
        } else {
            log.warn("No messages loaded from SurrealDB - catalog is empty for {} keys", failed);
        }
    }

    private static MessageModel toMessageModel(MessageKeyEnum key, MessageData data) {
        return new MessageModel(
                key,
                data.getContent(),
                data.getTitle(),
                parseType(data.getType().getName()),
                parseCategory(data.getCategory().getName())
        );
    }

    private static MessageTypeEnum parseType(String name) {
        if ("Technical".equalsIgnoreCase(name)) return MessageTypeEnum.TECHNICAL;
        return MessageTypeEnum.FUNCTIONAL;
    }

    private static MessageCategoryEnum parseCategory(String name) {
        if (isNullObject(name) || name.isBlank()) return MessageCategoryEnum.ERROR;
        return switch (name.toLowerCase()) {
            case "warning"      -> MessageCategoryEnum.WARNING;
            case "confirmation" -> MessageCategoryEnum.CONFIRMATION;
            case "information"  -> MessageCategoryEnum.INFORMATION;
            case "debug"        -> MessageCategoryEnum.DEBUG;
            default             -> MessageCategoryEnum.ERROR;
        };
    }
}
package co.edu.uco.core.application.catalog.strategy.inmemory;

import co.edu.uco.core.application.catalog.MessageModel;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.helper.UtilObject;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

@Component
public final class InMemoryMessageCatalog extends InMemoryCatalog {
    private Map<MessageKeyEnum, MessageModel> messages;
    @Override
    public MessageModel getMessageById(MessageKeyEnum code) {
        if (isNullObject(code)) {
            throw CrossWordsException.build(getContent(String.valueOf(MessageKeyEnum.TCH_007)));
        }
        return messages.get(code);
    }
    @Override
    public String getContent(String code) {
        if (isNullObject(code)) {
            throw CrossWordsException.build(getContent(String.valueOf(MessageKeyEnum.TCH_007)));
        }
        return messages.get(MessageKeyEnum.of(code)).content();
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
        for (var messageEnum : DetailMessageEnum.values()) {
            messages.put(messageEnum.getCode(), messageEnum.getMessage());
        }
    }
    @Override
    public void reloadCatalog() {
        messages.clear();
        loadCatalog();
    }
}
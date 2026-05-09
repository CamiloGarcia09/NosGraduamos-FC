package co.edu.uco.core.application.catalog;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageCategoryEnum;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageTypeEnum;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

public record MessageModel(MessageKeyEnum code, String content, String title, MessageTypeEnum type, MessageCategoryEnum category) {
    public MessageModel {
        if (isNullObject(code)) {
            throw new IllegalArgumentException(MessageKeyEnum.TCH_010.getKey());
        }
        if (isNullObject(content)) {
            throw new IllegalArgumentException(MessageKeyEnum.TCH_011.getKey());
        }
        if (isNullObject(title)) {
            throw new IllegalArgumentException(MessageKeyEnum.TCH_012.getKey());
        }
        if (isNullObject(type)) {
            throw new IllegalArgumentException(MessageKeyEnum.TCH_013.getKey());
        }
        if (isNullObject(category)) {
            throw new IllegalArgumentException(MessageKeyEnum.TCH_014.getKey());
        }
    }
}
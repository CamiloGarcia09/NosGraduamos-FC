package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.FunctionalityData;
import co.edu.uco.core.domain.data.MessageCategoryData;
import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.data.MessageTypeData;
import co.edu.uco.infrastructure.adapter.secondary.repository.redis.MessageRedis;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.SINGLETON_SCOPE;
import static co.edu.uco.utils.helper.UtilText.EMPTY;

@Component
@Scope(SINGLETON_SCOPE)
public final class MessageDataCacheMapper implements DataMapper<MessageData, MessageRedis> {
    @Override
    public MessageData mapperData(MessageRedis model) {
        return new MessageData(model.getId(), model.getCode(), model.getTitle(),
                model.getContent(), MessageTypeData.build(model.getType()),
                MessageCategoryData.build(model.getCategory()), model.getApplication(), FunctionalityData.build(model.getFunctionality()));
    }
    @Override
    public MessageRedis mapperModel(MessageData data) {
        return new MessageRedis(data.getId(), data.getCode(), data.getTitle(), data.getContent(),
                data.getType().getName(),
                data.getCategory().getName(), data.getStatus().getName(), data.getApplication(),
                data.getFunctionality().getName(), EMPTY);
    }
}
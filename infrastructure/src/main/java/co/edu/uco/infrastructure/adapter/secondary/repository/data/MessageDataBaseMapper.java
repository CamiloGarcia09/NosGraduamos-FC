package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.MessageDocument;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class MessageDataBaseMapper implements DataMapper<MessageData, MessageDocument> {
    private final ModelMapper modelMapper;
    public MessageDataBaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public MessageData mapperData(MessageDocument model) {
        return modelMapper.map(model, MessageData.class);
    }
    @Override
    public MessageDocument mapperModel(MessageData data) {
        return modelMapper.map(data, MessageDocument.class);
    }
}
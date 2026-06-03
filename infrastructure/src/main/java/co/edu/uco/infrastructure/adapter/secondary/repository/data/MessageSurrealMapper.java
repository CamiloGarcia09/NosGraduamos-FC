package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model.MessageSurrealModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class MessageSurrealMapper implements DataMapper<MessageData, MessageSurrealModel> {
    private final ModelMapper modelMapper;

    public MessageSurrealMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MessageData mapperData(MessageSurrealModel model) {
        return modelMapper.map(model, MessageData.class);
    }

    @Override
    public MessageSurrealModel mapperModel(MessageData data) {
        return modelMapper.map(data, MessageSurrealModel.class);
    }
}

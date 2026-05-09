package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.StatusTokenData;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.StatusTokenDocument;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class TokenStateDataMapper implements DataMapper<StatusTokenData, StatusTokenDocument> {
    private final ModelMapper mapper;
    public TokenStateDataMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public StatusTokenData mapperData(StatusTokenDocument model) {
        return mapper.map(model, StatusTokenData.class);
    }
    @Override
    public StatusTokenDocument mapperModel(StatusTokenData data) {
        return mapper.map(data, StatusTokenDocument.class);
    }
}
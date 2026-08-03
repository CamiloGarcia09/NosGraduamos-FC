package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model.EnvironmentSurrealModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class EnvironmentSurrealMapper implements DataMapper<EnvironmentData, EnvironmentSurrealModel> {
    private final ModelMapper modelMapper;

    public EnvironmentSurrealMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public EnvironmentData mapperData(EnvironmentSurrealModel model) {
        return modelMapper.map(model, EnvironmentData.class);
    }

    @Override
    public EnvironmentSurrealModel mapperModel(EnvironmentData data) {
        return modelMapper.map(data, EnvironmentSurrealModel.class);
    }
}

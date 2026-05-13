package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.StatusTokenData;
import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model.StatusTokenSurrealModel;
import org.springframework.stereotype.Component;

@Component
public final class TokenStateSurrealMapper implements DataMapper<StatusTokenData, StatusTokenSurrealModel> {

    @Override
    public StatusTokenData mapperData(final StatusTokenSurrealModel model) {
        return new StatusTokenData(model.getId(), model.getName());
    }

    @Override
    public StatusTokenSurrealModel mapperModel(final StatusTokenData data) {
        return new StatusTokenSurrealModel(data.getId(), data.getName());
    }
}

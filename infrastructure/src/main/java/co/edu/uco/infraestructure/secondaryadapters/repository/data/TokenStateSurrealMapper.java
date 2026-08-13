package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;
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

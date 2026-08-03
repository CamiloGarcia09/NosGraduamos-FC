package co.edu.uco.infrastructure.adapter.secondary.repository.surreal;

import co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model.StatusTokenSurrealModel;

public interface TokenStateSurrealRepositoryAdapter {

    StatusTokenSurrealModel findStatusTokenSurrealModelById(String id);

    StatusTokenSurrealModel findStatusTokenSurrealModelByName(String name);
}

package co.edu.uco.infraestructure.secondaryadapters.repository.surreal;

import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;

public interface TokenStateSurrealRepositoryAdapter {

    StatusTokenSurrealModel findStatusTokenSurrealModelById(String id);

    StatusTokenSurrealModel findStatusTokenSurrealModelByName(String name);
}

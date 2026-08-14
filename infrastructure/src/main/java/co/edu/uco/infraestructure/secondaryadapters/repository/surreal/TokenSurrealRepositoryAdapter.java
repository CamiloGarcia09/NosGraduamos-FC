package co.edu.uco.infraestructure.secondaryadapters.repository.surreal;

import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;

import java.util.Optional;

public interface TokenSurrealRepositoryAdapter {

    TokenSurrealModel upsert(TokenSurrealModel model);

    Optional<TokenSurrealModel> findTokenSurrealModelById(String id);

    Optional<TokenSurrealModel> findTokenSurrealModelByEnvironmentIdAndStateId(String environmentId, String stateId);
}

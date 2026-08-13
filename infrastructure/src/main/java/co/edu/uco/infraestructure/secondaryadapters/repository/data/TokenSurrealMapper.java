package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
import org.springframework.stereotype.Component;

@Component
public final class TokenSurrealMapper implements DataMapper<TokenData, TokenSurrealModel> {

    @Override
    public TokenData mapperData(final TokenSurrealModel model) {
        return new TokenData(
                model.getId(),
                model.getCreationDate(),
                model.getExpirationDate(),
                model.getEnvironmentId(),
                model.getSecretName(),
                model.getStateId()
        );
    }

    @Override
    public TokenSurrealModel mapperModel(final TokenData data) {
        return new TokenSurrealModel(
                data.getId(),
                data.getSecretName(),
                data.getCreationDate(),
                data.getExpirationDate(),
                data.getEnvironmentId(),
                data.getStateId()
        );
    }
}

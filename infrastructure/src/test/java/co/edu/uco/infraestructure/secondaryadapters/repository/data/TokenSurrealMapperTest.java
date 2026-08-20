package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TokenSurrealMapperTest {

    private final TokenSurrealMapper mapper = new TokenSurrealMapper();

    @Test
    void mapperData_mapsModelToData() {
        LocalDateTime creation = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime expiration = LocalDateTime.of(2026, 1, 1, 10, 0);
        TokenSurrealModel model = new TokenSurrealModel("id-1", "secret", creation, expiration, "env-1", "st-1");

        TokenData data = mapper.mapperData(model);

        assertThat(data.getId()).isEqualTo("id-1");
        assertThat(data.getSecretName()).isEqualTo("secret");
        assertThat(data.getCreationDate()).isEqualTo(creation);
        assertThat(data.getExpirationDate()).isEqualTo(expiration);
        assertThat(data.getEnvironmentId()).isEqualTo("env-1");
        assertThat(data.getStateId()).isEqualTo("st-1");
    }

    @Test
    void mapperModel_mapsDataToModel() {
        LocalDateTime creation = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime expiration = LocalDateTime.of(2026, 1, 1, 10, 0);
        TokenData data = new TokenData("id-1", creation, expiration, "env-1", "secret", "st-1");

        TokenSurrealModel model = mapper.mapperModel(data);

        assertThat(model.getId()).isEqualTo("id-1");
        assertThat(model.getSecretName()).isEqualTo("secret");
        assertThat(model.getCreationDate()).isEqualTo(creation);
        assertThat(model.getExpirationDate()).isEqualTo(expiration);
        assertThat(model.getEnvironmentId()).isEqualTo("env-1");
        assertThat(model.getStateId()).isEqualTo("st-1");
    }
}
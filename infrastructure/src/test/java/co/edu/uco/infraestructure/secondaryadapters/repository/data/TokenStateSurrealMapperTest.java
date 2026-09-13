package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenStateSurrealMapperTest {

    private final TokenStateSurrealMapper mapper = new TokenStateSurrealMapper();

    @Test
    void mapperData_mapsModelToData() {
        UUID id = UUID.randomUUID();
        StatusTokenSurrealModel model = new StatusTokenSurrealModel(id, "ACTIVE");

        StatusTokenData data = mapper.mapperData(model);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void mapperModel_mapsDataToModel() {
        UUID id = UUID.randomUUID();
        StatusTokenData data = new StatusTokenData(id, "ACTIVE");

        StatusTokenSurrealModel model = mapper.mapperModel(data);

        assertThat(model.getId()).isEqualTo(id);
        assertThat(model.getName()).isEqualTo("ACTIVE");
    }
}
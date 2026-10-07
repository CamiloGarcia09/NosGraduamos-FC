package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ExternalIdentitySurrealModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalIdentitySurrealMapperTest {

    private final ExternalIdentitySurrealMapper mapper = new ExternalIdentitySurrealMapper();

    @Test
    void mapperData_mapsModelToEntity_withEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "https://accounts.google.com", "subj-123", "user@google.com");

        ExternalIdentityEntity entity = mapper.mapperData(model);

        assertThat(entity)
                .extracting(ExternalIdentityEntity::getId, ExternalIdentityEntity::getIssuer,
                        ExternalIdentityEntity::getSubject, ExternalIdentityEntity::getEmail)
                .containsExactly(id, "https://accounts.google.com", "subj-123", "user@google.com");
    }

    @Test
    void mapperData_mapsModelToEntity_withNullEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "https://auth.internal", "svc-456", null);

        ExternalIdentityEntity entity = mapper.mapperData(model);

        assertThat(entity)
                .extracting(ExternalIdentityEntity::getId, ExternalIdentityEntity::getIssuer,
                        ExternalIdentityEntity::getSubject, ExternalIdentityEntity::getEmail)
                .containsExactly(id, "https://auth.internal", "svc-456", null);
    }

    @Test
    void mapperModel_mapsEntityToModel_withEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(id);
        entity.setIssuer("https://accounts.google.com");
        entity.setSubject("subj-123");
        entity.setEmail("user@google.com");

        ExternalIdentitySurrealModel model = mapper.mapperModel(entity);

        assertThat(model)
                .extracting(ExternalIdentitySurrealModel::getId, ExternalIdentitySurrealModel::getIssuer,
                        ExternalIdentitySurrealModel::getSubject, ExternalIdentitySurrealModel::getEmail)
                .containsExactly(id, "https://accounts.google.com", "subj-123", "user@google.com");
    }

    @Test
    void mapperModel_mapsEntityToModel_withNullEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(id);
        entity.setIssuer("https://auth.internal");
        entity.setSubject("svc-456");
        entity.setEmail(null);

        ExternalIdentitySurrealModel model = mapper.mapperModel(entity);

        assertThat(model)
                .extracting(ExternalIdentitySurrealModel::getId, ExternalIdentitySurrealModel::getIssuer,
                        ExternalIdentitySurrealModel::getSubject, ExternalIdentitySurrealModel::getEmail)
                .containsExactly(id, "https://auth.internal", "svc-456", null);
    }

    @Test
    void mapperData_roundtrip_preservesAllFields() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel original = new ExternalIdentitySurrealModel(
                id, "issuer", "subject", "mail@test.com");

        ExternalIdentityEntity entity = mapper.mapperData(original);
        ExternalIdentitySurrealModel roundtripped = mapper.mapperModel(entity);

        assertThat(roundtripped)
                .usingRecursiveComparison()
                .isEqualTo(original);
    }
}

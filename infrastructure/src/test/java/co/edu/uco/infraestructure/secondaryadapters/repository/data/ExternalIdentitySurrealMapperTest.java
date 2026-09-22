package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ExternalIdentitySurrealModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalIdentitySurrealMapperTest {

    private final ExternalIdentitySurrealMapper mapper = new ExternalIdentitySurrealMapper();

    @Test
    void mapperData_mapsModelToEntity_withHumanType() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "https://accounts.google.com", "subj-123", "user@google.com", PrincipalType.HUMAN);

        ExternalIdentityEntity entity = mapper.mapperData(model);

        assertThat(entity).satisfies(e -> {
            assertThat(e.getId()).isEqualTo(id);
            assertThat(e.getIssuer()).isEqualTo("https://accounts.google.com");
            assertThat(e.getSubject()).isEqualTo("subj-123");
            assertThat(e.getEmail()).isEqualTo("user@google.com");
            assertThat(e.getPrincipalType()).isEqualTo(PrincipalType.HUMAN);
        });
    }

    @Test
    void mapperData_mapsModelToEntity_withServiceTypeAndNullEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "https://auth.internal", "svc-456", null, PrincipalType.SERVICE);

        ExternalIdentityEntity entity = mapper.mapperData(model);

        assertThat(entity).satisfies(e -> {
            assertThat(e.getId()).isEqualTo(id);
            assertThat(e.getIssuer()).isEqualTo("https://auth.internal");
            assertThat(e.getSubject()).isEqualTo("svc-456");
            assertThat(e.getEmail()).isNull();
            assertThat(e.getPrincipalType()).isEqualTo(PrincipalType.SERVICE);
        });
    }

    @Test
    void mapperModel_mapsEntityToModel_withHumanType() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(id);
        entity.setIssuer("https://accounts.google.com");
        entity.setSubject("subj-123");
        entity.setEmail("user@google.com");
        entity.setPrincipalType(PrincipalType.HUMAN);

        ExternalIdentitySurrealModel model = mapper.mapperModel(entity);

        assertThat(model).satisfies(m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getIssuer()).isEqualTo("https://accounts.google.com");
            assertThat(m.getSubject()).isEqualTo("subj-123");
            assertThat(m.getEmail()).isEqualTo("user@google.com");
            assertThat(m.getPrincipalType()).isEqualTo(PrincipalType.HUMAN);
        });
    }

    @Test
    void mapperModel_mapsEntityToModel_withServiceTypeAndNullEmail() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(id);
        entity.setIssuer("https://auth.internal");
        entity.setSubject("svc-456");
        entity.setEmail(null);
        entity.setPrincipalType(PrincipalType.SERVICE);

        ExternalIdentitySurrealModel model = mapper.mapperModel(entity);

        assertThat(model).satisfies(m -> {
            assertThat(m.getId()).isEqualTo(id);
            assertThat(m.getIssuer()).isEqualTo("https://auth.internal");
            assertThat(m.getSubject()).isEqualTo("svc-456");
            assertThat(m.getEmail()).isNull();
            assertThat(m.getPrincipalType()).isEqualTo(PrincipalType.SERVICE);
        });
    }

    @Test
    void mapperData_roundtrip_preservesAllFields() {
        UUID id = UUID.randomUUID();
        ExternalIdentitySurrealModel original = new ExternalIdentitySurrealModel(
                id, "issuer", "subject", "mail@test.com", PrincipalType.HUMAN);

        ExternalIdentityEntity entity = mapper.mapperData(original);
        ExternalIdentitySurrealModel roundtripped = mapper.mapperModel(entity);

        assertThat(roundtripped).satisfies(m -> {
            assertThat(m.getId()).isEqualTo(original.getId());
            assertThat(m.getIssuer()).isEqualTo(original.getIssuer());
            assertThat(m.getSubject()).isEqualTo(original.getSubject());
            assertThat(m.getEmail()).isEqualTo(original.getEmail());
            assertThat(m.getPrincipalType()).isEqualTo(original.getPrincipalType());
        });
    }
}

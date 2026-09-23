package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ActiveContextSurrealModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveContextSurrealMapperTest {

    private final ActiveContextSurrealMapper mapper = new ActiveContextSurrealMapper();

    @Test
    void mapperData_mapsEveryFieldExplicitly() {
        UUID id = UUID.randomUUID();
        UUID identityId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();
        LocalDateTime updatedAt = LocalDateTime.of(2026, 9, 22, 10, 15);
        ActiveContextSurrealModel model = new ActiveContextSurrealModel(
                id, identityId, organizationId, applicationId, environmentId, updatedAt);

        ActiveContextEntity result = mapper.mapperData(model);

        assertThat(result).satisfies(context -> {
            assertThat(context.getId()).isEqualTo(id);
            assertThat(context.getExternalIdentityId()).isEqualTo(identityId);
            assertThat(context.getOrganizationId()).isEqualTo(organizationId);
            assertThat(context.getApplicationId()).isEqualTo(applicationId);
            assertThat(context.getEnvironmentId()).isEqualTo(environmentId);
            assertThat(context.getUpdatedAt()).isEqualTo(updatedAt);
        });
    }

    @Test
    void mapperModel_mapsEveryFieldExplicitly() {
        ActiveContextEntity context = context();

        ActiveContextSurrealModel result = mapper.mapperModel(context);

        assertThat(result).satisfies(model -> {
            assertThat(model.getId()).isEqualTo(context.getId());
            assertThat(model.getExternalIdentityId()).isEqualTo(context.getExternalIdentityId());
            assertThat(model.getOrganizationId()).isEqualTo(context.getOrganizationId());
            assertThat(model.getApplicationId()).isEqualTo(context.getApplicationId());
            assertThat(model.getEnvironmentId()).isEqualTo(context.getEnvironmentId());
            assertThat(model.getUpdatedAt()).isEqualTo(context.getUpdatedAt());
        });
    }

    private ActiveContextEntity context() {
        ActiveContextEntity context = new ActiveContextEntity();
        context.setId(UUID.randomUUID());
        context.setExternalIdentityId(UUID.randomUUID());
        context.setOrganizationId(UUID.randomUUID());
        context.setApplicationId(UUID.randomUUID());
        context.setEnvironmentId(UUID.randomUUID());
        context.setUpdatedAt(LocalDateTime.of(2026, 9, 22, 10, 15));
        return context;
    }
}

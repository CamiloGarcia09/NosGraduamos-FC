package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ActiveContextSurrealModelTest {

    @Test
    void fullConstructor_preservesAllValues() {
        UUID id = UUID.randomUUID();
        UUID identityId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();
        LocalDateTime updatedAt = LocalDateTime.of(2026, 9, 22, 12, 30);

        ActiveContextSurrealModel model = new ActiveContextSurrealModel(
                id, identityId, organizationId, applicationId, environmentId, updatedAt);

        assertSoftly(softly -> {
            softly.assertThat(model.getId()).isEqualTo(id);
            softly.assertThat(model.getExternalIdentityId()).isEqualTo(identityId);
            softly.assertThat(model.getOrganizationId()).isEqualTo(organizationId);
            softly.assertThat(model.getApplicationId()).isEqualTo(applicationId);
            softly.assertThat(model.getEnvironmentId()).isEqualTo(environmentId);
            softly.assertThat(model.getUpdatedAt()).isEqualTo(updatedAt);
        });
    }

    @Test
    void defaultConstructor_usesSafeDefaults() {
        LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC);

        ActiveContextSurrealModel model = new ActiveContextSurrealModel();

        assertSoftly(softly -> {
            softly.assertThat(model.getId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(model.getExternalIdentityId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(model.getOrganizationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(model.getApplicationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(model.getEnvironmentId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(model.getUpdatedAt()).isBetween(before, LocalDateTime.now(ZoneOffset.UTC));
        });
    }
}

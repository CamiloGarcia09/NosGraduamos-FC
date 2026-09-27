package co.edu.uco.application.usecase.domain.aggregate.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ActiveContextEntityTest {

    @Test
    void setters_preserveContextIdentifiers() {
        ActiveContextEntity activeContext = new ActiveContextEntity();
        UUID id = UUID.randomUUID();
        UUID externalIdentityId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();

        activeContext.setId(id);
        activeContext.setExternalIdentityId(externalIdentityId);
        activeContext.setOrganizationId(organizationId);
        activeContext.setApplicationId(applicationId);
        activeContext.setEnvironmentId(environmentId);

        assertSoftly(softly -> {
            softly.assertThat(activeContext.getId()).isEqualTo(id);
            softly.assertThat(activeContext.getExternalIdentityId()).isEqualTo(externalIdentityId);
            softly.assertThat(activeContext.getOrganizationId()).isEqualTo(organizationId);
            softly.assertThat(activeContext.getApplicationId()).isEqualTo(applicationId);
            softly.assertThat(activeContext.getEnvironmentId()).isEqualTo(environmentId);
        });
    }

    @Test
    void setters_useDefaultIdentifiers_whenValuesAreMissing() {
        ActiveContextEntity activeContext = new ActiveContextEntity();

        activeContext.setId(null);
        activeContext.setExternalIdentityId(null);
        activeContext.setOrganizationId(null);
        activeContext.setApplicationId(null);
        activeContext.setEnvironmentId(null);

        assertSoftly(softly -> {
            softly.assertThat(activeContext.getId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(activeContext.getExternalIdentityId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(activeContext.getOrganizationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(activeContext.getApplicationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(activeContext.getEnvironmentId()).isEqualTo(DEFAULT_UUID);
        });
    }

    @Test
    void setUpdatedAt_preservesTimestamp() {
        ActiveContextEntity activeContext = new ActiveContextEntity();
        LocalDateTime updatedAt = LocalDateTime.of(2026, 9, 22, 14, 30, 45);

        activeContext.setUpdatedAt(updatedAt);

        assertThat(activeContext.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void setUpdatedAt_usesCurrentUtcTime_whenTimestampIsMissing() {
        ActiveContextEntity activeContext = new ActiveContextEntity();
        LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC);

        activeContext.setUpdatedAt(null);

        assertThat(activeContext.getUpdatedAt())
                .isBetween(before, LocalDateTime.now(ZoneOffset.UTC));
    }
}

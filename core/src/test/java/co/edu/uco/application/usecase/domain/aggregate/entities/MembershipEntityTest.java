package co.edu.uco.application.usecase.domain.aggregate.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MembershipEntityTest {

    @Test
    void setters_preserveMembershipIdentifiers() {
        MembershipEntity membership = new MembershipEntity();
        UUID id = UUID.randomUUID();
        UUID externalIdentityId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        membership.setId(id);
        membership.setExternalIdentityId(externalIdentityId);
        membership.setOrganizationId(organizationId);

        assertSoftly(softly -> {
            softly.assertThat(membership.getId()).isEqualTo(id);
            softly.assertThat(membership.getExternalIdentityId()).isEqualTo(externalIdentityId);
            softly.assertThat(membership.getOrganizationId()).isEqualTo(organizationId);
        });
    }

    @Test
    void setters_useDefaultIdentifiers_whenValuesAreMissing() {
        MembershipEntity membership = new MembershipEntity();

        membership.setId(null);
        membership.setExternalIdentityId(null);
        membership.setOrganizationId(null);

        assertSoftly(softly -> {
            softly.assertThat(membership.getId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(membership.getExternalIdentityId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(membership.getOrganizationId()).isEqualTo(DEFAULT_UUID);
        });
    }
}

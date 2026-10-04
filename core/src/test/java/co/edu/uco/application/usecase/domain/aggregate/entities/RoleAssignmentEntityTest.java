package co.edu.uco.application.usecase.domain.aggregate.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RoleAssignmentEntityTest {

    @Test
    void setters_preserveOrganizationScopedAssignmentIdentifiers() {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();
        UUID id = UUID.randomUUID();
        UUID membershipId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        assignment.setId(id);
        assignment.setMembershipId(membershipId);
        assignment.setRoleId(roleId);
        assignment.setOrganizationId(organizationId);

        assertSoftly(softly -> {
            softly.assertThat(assignment.getId()).isEqualTo(id);
            softly.assertThat(assignment.getMembershipId()).isEqualTo(membershipId);
            softly.assertThat(assignment.getRoleId()).isEqualTo(roleId);
            softly.assertThat(assignment.getOrganizationId()).isEqualTo(organizationId);
        });
    }

    @Test
    void setters_useDefaultIdentifiers_whenValuesAreMissing() {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();

        assignment.setId(null);
        assignment.setMembershipId(null);
        assignment.setRoleId(null);
        assignment.setOrganizationId(null);

        assertSoftly(softly -> {
            softly.assertThat(assignment.getId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getMembershipId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getRoleId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getOrganizationId()).isEqualTo(DEFAULT_UUID);
        });
    }
}

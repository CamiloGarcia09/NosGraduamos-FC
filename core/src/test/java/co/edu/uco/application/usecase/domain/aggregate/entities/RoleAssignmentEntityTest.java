package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.security.AuthorizationScopeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RoleAssignmentEntityTest {

    @Test
    void setters_preserveAssignmentAndScopeHierarchyIdentifiers() {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();
        UUID id = UUID.randomUUID();
        UUID membershipId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();

        assignment.setId(id);
        assignment.setMembershipId(membershipId);
        assignment.setRoleId(roleId);
        assignment.setOrganizationId(organizationId);
        assignment.setApplicationId(applicationId);
        assignment.setEnvironmentId(environmentId);

        assertSoftly(softly -> {
            softly.assertThat(assignment.getId()).isEqualTo(id);
            softly.assertThat(assignment.getMembershipId()).isEqualTo(membershipId);
            softly.assertThat(assignment.getRoleId()).isEqualTo(roleId);
            softly.assertThat(assignment.getOrganizationId()).isEqualTo(organizationId);
            softly.assertThat(assignment.getApplicationId()).isEqualTo(applicationId);
            softly.assertThat(assignment.getEnvironmentId()).isEqualTo(environmentId);
        });
    }

    @Test
    void setters_useDefaultIdentifiers_whenValuesAreMissing() {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();

        assignment.setId(null);
        assignment.setMembershipId(null);
        assignment.setRoleId(null);
        assignment.setOrganizationId(null);
        assignment.setApplicationId(null);
        assignment.setEnvironmentId(null);

        assertSoftly(softly -> {
            softly.assertThat(assignment.getId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getMembershipId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getRoleId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getOrganizationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getApplicationId()).isEqualTo(DEFAULT_UUID);
            softly.assertThat(assignment.getEnvironmentId()).isEqualTo(DEFAULT_UUID);
        });
    }

    @ParameterizedTest
    @EnumSource(AuthorizationScopeType.class)
    void setScopeType_preservesEverySupportedScope(final AuthorizationScopeType scopeType) {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();

        assignment.setScopeType(scopeType);

        assertThat(assignment.getScopeType()).isEqualTo(scopeType);
    }

    @Test
    void setScopeType_acceptsMissingValue() {
        RoleAssignmentEntity assignment = new RoleAssignmentEntity();
        assignment.setScopeType(AuthorizationScopeType.ENVIRONMENT);

        assignment.setScopeType(null);

        assertThat(assignment.getScopeType()).isNull();
    }
}

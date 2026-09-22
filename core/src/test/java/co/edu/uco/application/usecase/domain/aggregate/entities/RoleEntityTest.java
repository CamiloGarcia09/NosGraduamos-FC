package co.edu.uco.application.usecase.domain.aggregate.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityTest {

    @Test
    void setId_preservesProvidedIdentifier() {
        RoleEntity role = new RoleEntity();
        UUID id = UUID.randomUUID();

        role.setId(id);

        assertThat(role.getId()).isEqualTo(id);
    }

    @Test
    void setId_usesDefaultIdentifier_whenValueIsMissing() {
        RoleEntity role = new RoleEntity();

        role.setId(null);

        assertThat(role.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void setName_trimsProvidedValue() {
        RoleEntity role = new RoleEntity();

        role.setName("  Organization administrator  ");

        assertThat(role.getName()).isEqualTo("Organization administrator");
    }

    @Test
    void setName_usesEmptyValue_whenValueIsMissing() {
        RoleEntity role = new RoleEntity();

        role.setName(null);

        assertThat(role.getName()).isEmpty();
    }
}

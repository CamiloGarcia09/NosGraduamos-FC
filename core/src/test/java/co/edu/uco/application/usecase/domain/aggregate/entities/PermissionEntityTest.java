package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.security.PermissionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionEntityTest {

    @Test
    void setId_preservesProvidedIdentifier() {
        PermissionEntity permission = new PermissionEntity();
        UUID id = UUID.randomUUID();

        permission.setId(id);

        assertThat(permission.getId()).isEqualTo(id);
    }

    @Test
    void setId_usesDefaultIdentifier_whenValueIsMissing() {
        PermissionEntity permission = new PermissionEntity();

        permission.setId(null);

        assertThat(permission.getId()).isEqualTo(DEFAULT_UUID);
    }

    @ParameterizedTest
    @EnumSource(PermissionCode.class)
    void setCode_preservesEverySupportedPermission(final PermissionCode code) {
        PermissionEntity permission = new PermissionEntity();

        permission.setCode(code);

        assertThat(permission.getCode()).isEqualTo(code);
    }

    @Test
    void setCode_acceptsMissingValue() {
        PermissionEntity permission = new PermissionEntity();
        permission.setCode(PermissionCode.MESSAGE_READ);

        permission.setCode(null);

        assertThat(permission.getCode()).isNull();
    }
}

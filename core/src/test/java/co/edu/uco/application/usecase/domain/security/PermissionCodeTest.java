package co.edu.uco.application.usecase.domain.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionCodeTest {

    @Test
    void values_containsSupportedPermissionsInStableOrder() {
        assertThat(PermissionCode.values()).containsExactly(
                PermissionCode.CONTEXT_SELECT,
                PermissionCode.MESSAGE_READ,
                PermissionCode.MESSAGE_CREATE,
                PermissionCode.MESSAGE_TRANSLATE,
                PermissionCode.APPLICATION_CREATE,
                PermissionCode.ENVIRONMENT_CREATE,
                PermissionCode.FUNCTIONALITY_CREATE
        );
    }
}

package co.edu.uco.application.usecase.domain.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationScopeTypeTest {

    @Test
    void values_containsSupportedScopeHierarchyInStableOrder() {
        assertThat(AuthorizationScopeType.values()).containsExactly(
                AuthorizationScopeType.ORGANIZATION,
                AuthorizationScopeType.APPLICATION,
                AuthorizationScopeType.ENVIRONMENT
        );
    }
}

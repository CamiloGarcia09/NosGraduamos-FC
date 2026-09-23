package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ExternalIdentityRequiredRuleImplTest {

    private final CatalogPort catalogPort = mock(CatalogPort.class);
    private final ExternalIdentityRequiredRuleImpl rule = new ExternalIdentityRequiredRuleImpl(catalogPort);

    @Test
    void validate_acceptsPresentIdentityWithoutCatalogLookup() {
        ExternalIdentity identity = new ExternalIdentity("issuer", "subject", null, PrincipalType.HUMAN, Instant.MAX);

        assertThatCode(() -> rule.validate(identity)).doesNotThrowAnyException();
        verifyNoInteractions(catalogPort);
    }

    @Test
    void validate_throwsUnauthorizedUsingFun152WhenIdentityIsMissing() {
        when(catalogPort.getMessage("FUN_152")).thenReturn("Authentication required");

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(UnauthorizedException.class)
                .extracting("httpStatus", "userMessage").containsExactly(401, "Authentication required");
        verify(catalogPort).getMessage("FUN_152");
    }
}

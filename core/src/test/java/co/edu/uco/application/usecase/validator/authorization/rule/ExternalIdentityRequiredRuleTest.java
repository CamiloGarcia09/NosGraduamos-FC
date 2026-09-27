package co.edu.uco.application.usecase.validator.authorization.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalIdentityRequiredRuleTest {

    private static final String AUTH_REQUIRED_MESSAGE = "Authentication required";

    @Mock
    private CatalogPort catalogPort;

    private ExternalIdentityRequiredRule rule;

    @BeforeEach
    void setUp() {
        rule = new ExternalIdentityRequiredRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenIdentityIsPresent() {
        ExternalIdentity identity = identity(Instant.MAX);

        assertThatCode(() -> rule.validate(identity)).doesNotThrowAnyException();

        verifyNoInteractions(catalogPort);
    }

    @ParameterizedTest(name = "[{0}] identity is accepted")
    @EnumSource(PrincipalType.class)
    void validate_doesNotThrow_forAnyPrincipalType(PrincipalType principalType) {
        ExternalIdentity identity = new ExternalIdentity("issuer", "subject", null, principalType, Instant.MAX);

        assertThatCode(() -> rule.validate(identity)).doesNotThrowAnyException();

        verifyNoInteractions(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenIdentityHasNoExpiration() {
        ExternalIdentity identity = new ExternalIdentity("issuer", "subject", "user@example.com",
                PrincipalType.HUMAN, null);

        assertThatCode(() -> rule.validate(identity)).doesNotThrowAnyException();

        verifyNoInteractions(catalogPort);
    }

    @Test
    void validate_throwsUnauthorizedUsingFun152_whenIdentityIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_152.getCode()))
                .thenReturn(AUTH_REQUIRED_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(exception -> assertThat((UnauthorizedException) exception)
                        .extracting(UnauthorizedException::getHttpStatus, UnauthorizedException::getUserMessage)
                        .containsExactly(401, AUTH_REQUIRED_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_152.getCode());
    }

    private static ExternalIdentity identity(Instant expiration) {
        return new ExternalIdentity("issuer", "subject", "user@example.com", PrincipalType.HUMAN, expiration);
    }
}

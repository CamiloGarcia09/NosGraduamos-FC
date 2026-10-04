package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.secondaryports.ErrorResponse;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.ExternalIdentityResolverPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalIdentityInterceptorTest {

    @Mock
    private ExternalIdentityResolverPort identityResolver;
    @Mock
    private SerializerRegistry serializerRegistry;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private SerializerType serializer;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private ExternalIdentityInterceptor interceptor;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() {
        interceptor = new ExternalIdentityInterceptor(identityResolver, serializerRegistry, catalogPort);
    }

    @Test
    void preHandle_preservesLegacyFlow_whenAuthorizationHeaderIsMissing() throws Exception {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(identityResolver, never()).resolve(any());
    }

    @Test
    void preHandle_preservesLegacyFlow_whenAuthorizationHeaderIsBlank() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(" ");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(identityResolver, never()).resolve(any());
    }

    @Test
    void preHandle_setsExternalIdentity_whenBearerTokenIsValid() throws Exception {
        var identity = new ExternalIdentity("issuer", "subject", "email",
                Instant.parse("2030-01-01T00:00:00Z"));
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(identityResolver.resolve("valid-token")).thenReturn(identity);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(request).setAttribute("externalIdentity", identity);
    }

    @Test
    void preHandle_acceptsCaseInsensitiveBearerScheme() throws Exception {
        var identity = new ExternalIdentity("issuer", "subject", "service@example.com",
                Instant.parse("2030-01-01T00:00:00Z"));
        when(request.getHeader("Authorization")).thenReturn("bearer valid-token");
        when(identityResolver.resolve("valid-token")).thenReturn(identity);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(request).setAttribute("externalIdentity", identity);
    }

    @Test
    void preHandle_returns401_whenAuthorizationHeaderIsMalformed() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer  token-with-extra-space");
        stubUnauthorizedResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(401);
        verifyUnauthorizedResponse();
        verify(identityResolver, never()).resolve(any());
    }

    @Test
    void preHandle_returns401_whenResolverRejectsToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
        when(identityResolver.resolve("expired-token"))
                .thenThrow(UnauthorizedException.buildUserException("invalid or expired"));
        stubUnauthorizedResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(401);
        verifyUnauthorizedResponse();
    }

    private void stubUnauthorizedResponse() throws Exception {
        responseBody = new StringWriter();
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(catalogPort.getMessage("TCH_031")).thenReturn("Invalid token");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        when(serializer.serialize(any(ErrorResponse.class))).thenReturn("unauthorized");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    private void verifyUnauthorizedResponse() throws Exception {
        var errorResponseCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(serializer).serialize(errorResponseCaptor.capture());
        verify(response).setContentType("application/json");
        assertThat(responseBody).hasToString("unauthorized");
        assertThat(errorResponseCaptor.getValue().errors())
                .singleElement()
                .extracting("code", "message")
                .containsExactly("UNAUTHORIZED", "Invalid token");
    }
}

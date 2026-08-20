package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.primaryports.facade.token.FindEnvironmentIdTokenUseCaseFacade;
import co.edu.uco.application.primaryports.facade.token.VerifyAccessUseCaseFacade;
import co.edu.uco.application.secondaryports.Response;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenHeaderInterceptorTest {

    @Mock
    private SerializerRegistry serializerRegistry;
    @Mock
    private VerifyAccessUseCaseFacade verifyAccessUseCaseFacade;
    @Mock
    private FindEnvironmentIdTokenUseCaseFacade findEnvironmentIdTokenUseCaseFacade;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private SerializerType serializer;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private TokenHeaderInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new TokenHeaderInterceptor(
                serializerRegistry, verifyAccessUseCaseFacade, findEnvironmentIdTokenUseCaseFacade, catalogPort);
    }

    private void stubErrorResponse(String acceptHeader) throws Exception {
        when(serializerRegistry.getSerializerForMediaType(acceptHeader)).thenReturn(serializer);
        when(serializer.serialize(org.mockito.ArgumentMatchers.any(Response.class)))
                .thenReturn("{\"error\":\"forbidden\"}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    void preHandle_setsEnvironmentIdAndReturnsTrue_whenTokenIsValid() throws Exception {
        when(request.getHeader("Token")).thenReturn("valid-token");
        when(verifyAccessUseCaseFacade.execute("valid-token")).thenReturn(true);
        when(findEnvironmentIdTokenUseCaseFacade.execute("valid-token")).thenReturn("env-1");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(request).setAttribute("environmentId", "env-1");
    }

    @Test
    void preHandle_returnsFalse_whenTokenIsMissing() throws Exception {
        when(request.getHeader("Token")).thenReturn(" ");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(catalogPort.getMessage("TCH_032")).thenReturn("Token is required");
        stubErrorResponse("application/json");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(403);
    }

    @Test
    void preHandle_returnsFalse_whenAccessDenied() throws Exception {
        when(request.getHeader("Token")).thenReturn("invalid-token");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(verifyAccessUseCaseFacade.execute("invalid-token")).thenReturn(false);
        when(catalogPort.getMessage("TCH_031")).thenReturn("Access denied");
        stubErrorResponse("application/json");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(403);
    }
}
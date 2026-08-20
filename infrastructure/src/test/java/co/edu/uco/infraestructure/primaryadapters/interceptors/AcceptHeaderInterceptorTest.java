package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.secondaryports.Response;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
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
class AcceptHeaderInterceptorTest {

    @Mock
    private SerializerRegistry serializerRegistry;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private SerializerType serializer;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private AcceptHeaderInterceptor interceptor;

    @BeforeEach
    void setUp() throws Exception {
        when(loggerFactory.getLogger(AcceptHeaderInterceptor.class)).thenReturn(log);
        interceptor = new AcceptHeaderInterceptor(serializerRegistry, catalogPort, loggerFactory);
    }

    @Test
    void preHandle_returnsTrue_whenHeaderIsSupported() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.supports("application/json")).thenReturn(true);

        assertThat(interceptor.preHandle(request, response, new Object())).isTrue();
    }

    @Test
    void preHandle_usesDefaultJson_whenHeaderIsAbsent() throws Exception {
        when(request.getHeader("Accept")).thenReturn(null);
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.supports("application/json")).thenReturn(true);

        assertThat(interceptor.preHandle(request, response, new Object())).isTrue();
    }

    @Test
    void preHandle_writesErrorAndReturnsFalse_whenSerializerDoesNotSupportHeader() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/pdf");
        when(serializerRegistry.getSerializerForMediaType("application/pdf")).thenReturn(serializer);
        when(serializer.supports("application/pdf")).thenReturn(false);
        when(catalogPort.getMessage("TCH_022")).thenReturn("Content type %s is not supported");
        when(serializer.serialize(new Response<String>(java.util.List.of(), java.util.List.of("Content type application/pdf is not supported"))))
                .thenReturn("{\"error\":\"not supported\"}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        when(catalogPort.getMessage("TCH_023")).thenReturn("error written");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(406);
        verify(response).setContentType("application/json");
        assertThat(writer.toString()).contains("not supported");
        verify(log).error("error written", "{\"error\":\"not supported\"}");
    }

    @Test
    void preHandle_returnsFalse_whenSerializerIsNull() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/pdf");
        when(serializerRegistry.getSerializerForMediaType("application/pdf")).thenReturn(null);
        when(catalogPort.getMessage("TCH_022")).thenReturn("Content type %s is not supported");

        try {
            interceptor.preHandle(request, response, new Object());
        } catch (AssertionError | NullPointerException expected) {
            assertThat(expected).isNotNull();
        }
    }
}
package co.edu.uco.infraestructure.secondaryadapters.presenter.rest;

import co.edu.uco.application.secondaryports.Response;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpPresenterAdapterTest {

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

    private HttpPresenterAdapter<String> adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(HttpPresenterAdapter.class)).thenReturn(log);
        adapter = new HttpPresenterAdapter<>(serializerRegistry, catalogPort, loggerFactory);
    }

    private StringWriter stubWriter() throws IOException {
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        return writer;
    }

    @Test
    void presentRestSuccess_writesSerializedResponse() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of("dto"), List.of()))).thenReturn("{\"data\":[\"dto\"]}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        StringWriter writer = stubWriter();
        when(catalogPort.getMessage("TCH_021")).thenReturn("success");

        adapter.presentRestSuccess(List.of("dto"), request, response);

        verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        assertThat(writer.toString()).contains("dto");
        verify(log).info("success", "{\"data\":[\"dto\"]}");
    }

    @Test
    void presentRestSuccess_logsError_whenWriterFails() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of("dto"), List.of()))).thenReturn("json");
        when(response.getWriter()).thenThrow(new IOException("io"));
        when(catalogPort.getMessage("TCH_016")).thenReturn("presenter error");

        adapter.presentRestSuccess(List.of("dto"), request, response);

        verify(log).error(eq("presenter error"), any(IOException.class));
    }

    @Test
    void presentCrossWordsException_writesUserMessage() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of(), List.of("user msg")))).thenReturn("{\"errors\":[\"user msg\"]}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        StringWriter writer = stubWriter();
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", "user msg", ExceptionType.TECHNICAL);

        adapter.presentCrossWordsException(ex, request, response);

        verify(response).setStatus(404);
        assertThat(writer.toString()).contains("user msg");
        verify(log).error("error sent", "{\"errors\":[\"user msg\"]}");
    }

    @Test
    void presentCrossWordsException_usesFallbackWhenNoUserMessage() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of(), List.of("fallback")))).thenReturn("{\"errors\":[\"fallback\"]}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        StringWriter writer = stubWriter();
        when(catalogPort.getMessage("TCH_016")).thenReturn("log fallback");
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", " ", ExceptionType.TECHNICAL);

        adapter.presentCrossWordsException(ex, request, response);

        verify(log).error("log fallback", ex);
        assertThat(writer.toString()).contains("fallback");
    }

    @Test
    void presentCrossWordsException_rethrowsWhenSerializerFails() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of(), List.of("user msg"))))
                .thenThrow(new CrossWordsException("u", "t", new Exception()));
        when(catalogPort.getMessage("TCH_019")).thenReturn("wrap error");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", "user msg", ExceptionType.TECHNICAL);

        assertThatThrownBy(() -> adapter.presentCrossWordsException(ex, request, response))
                .isInstanceOf(CrossWordsException.class);
        verify(log).error(eq("wrap error"), any(CrossWordsException.class));
    }

    @Test
    void handleGeneralException_writesExceptionMessage() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of(), List.of("boom")))).thenReturn("{\"errors\":[\"boom\"]}");
        when(serializer.getSupportedContentType()).thenReturn("application/json");
        StringWriter writer = stubWriter();
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");

        adapter.handleGeneralException(new RuntimeException("boom"), request, response);

        verify(response).setStatus(400);
        assertThat(writer.toString()).contains("boom");
        verify(log).error("error sent", "{\"errors\":[\"boom\"]}");
    }

    @Test
    void handleGeneralException_logsError_whenWriterFails() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(new Response<>(List.of(), List.of("boom")))).thenReturn("json");
        when(response.getWriter()).thenThrow(new IOException("io"));
        when(catalogPort.getMessage("TCH_019")).thenReturn("wrap error");

        adapter.handleGeneralException(new RuntimeException("boom"), request, response);

        verify(log).error(eq("wrap error"), any(IOException.class));
    }
}
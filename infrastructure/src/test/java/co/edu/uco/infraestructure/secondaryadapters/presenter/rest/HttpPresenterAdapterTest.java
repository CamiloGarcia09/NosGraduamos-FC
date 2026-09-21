package co.edu.uco.infraestructure.secondaryadapters.presenter.rest;

import co.edu.uco.application.secondaryports.ErrorResponse;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

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

    private void stubCommonErrorHandling(String serializedBody) throws IOException {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(any(ErrorResponse.class))).thenReturn(serializedBody);
        when(serializer.getSupportedContentType()).thenReturn("application/json");
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
    void presentCrossWordsException_writesUserMessageWithHttpStatus() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"user msg\"}]}");
        when(catalogPort.getMessage("TCH_016")).thenReturn("log technical");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", "user msg", ExceptionType.TECHNICAL);

        adapter.presentCrossWordsException(ex, request, response);

        verify(response).setStatus(500);
        verify(response).setContentType("application/json");
        assertThat(writer.toString()).contains("user msg");
        verify(log).error("error sent", "{\"errors\":[{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"user msg\"}]}");
    }

    @Test
    void presentCrossWordsException_businessTypeMapsTo400() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{}");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.build("tech", "user msg", null,
                ExceptionType.BUSINESS, co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation.APPLICATION);

        adapter.presentCrossWordsException(ex, request, response);

        verify(response).setStatus(400);
    }

    @Test
    void presentCrossWordsException_usesFallbackWhenNoUserMessage() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"message\":\"fallback\"}]}");
        when(catalogPort.getMessage("TCH_016")).thenReturn("log fallback");
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", " ", ExceptionType.TECHNICAL);

        adapter.presentCrossWordsException(ex, request, response);

        verify(response).setStatus(500);
        verify(log).error("log fallback", ex);
        assertThat(writer.toString()).contains("fallback");
    }

    @Test
    void presentCrossWordsException_rethrowsWhenSerializerFails() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(any(ErrorResponse.class)))
                .thenThrow(new CrossWordsException("u", "t", new Exception()));
        when(catalogPort.getMessage("TCH_019")).thenReturn("wrap error");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", "user msg", ExceptionType.TECHNICAL);

        assertThatThrownBy(() -> adapter.presentCrossWordsException(ex, request, response))
                .isInstanceOf(CrossWordsException.class);
        verify(log).error(eq("wrap error"), any(CrossWordsException.class));
    }

    @Test
    void handleGeneralException_returns500WithGenericMessage_andLogsCause() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"fallback\"}]}");
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_016")).thenReturn("generic error");

        adapter.handleGeneralException(new RuntimeException("boom"), request, response);

        verify(response).setStatus(500);
        assertThat(writer.toString()).contains("fallback");
        assertThat(writer.toString()).doesNotContain("boom");
        verify(log).error(eq("generic error"), any(RuntimeException.class));
    }

    @Test
    void handleUnreadableBody_returns400() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"code\":\"BAD_REQUEST\",\"message\":\"fallback\"}]}");
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_016")).thenReturn("bad request");

        adapter.handleUnreadableBody(new HttpMessageNotReadableException("boom"), request, response);

        verify(response).setStatus(400);
        assertThat(writer.toString()).contains("BAD_REQUEST");
    }

    @Test
    void handleNotAcceptable_returns406() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{}");
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_016")).thenReturn("not acceptable");

        adapter.handleNotAcceptable(new HttpMediaTypeNotAcceptableException("boom"), request, response);

        verify(response).setStatus(406);
    }

    @Test
    void presentCrossWordsException_usesSemanticCodeWhenSet() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"code\":\"MESSAGE_NOT_FOUND\",\"message\":\"user msg\"}]}");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.build("tech", "user msg", null,
                ExceptionType.BUSINESS, co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation.APPLICATION);
        ex.setCode("MESSAGE_NOT_FOUND");

        adapter.presentCrossWordsException(ex, request, response);

        assertThat(writer.toString()).contains("MESSAGE_NOT_FOUND");
        verify(log).error("error sent", "{\"errors\":[{\"code\":\"MESSAGE_NOT_FOUND\",\"message\":\"user msg\"}]}");
    }

    @Test
    void presentCrossWordsException_usesStatusCodeWhenNoSemanticCode() throws Exception {
        StringWriter writer = stubWriter();
        stubCommonErrorHandling("{\"errors\":[{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"user msg\"}]}");
        when(catalogPort.getMessage("TCH_016")).thenReturn("log technical");
        when(catalogPort.getMessage("TCH_020")).thenReturn("error sent");
        CrossWordsException ex = CrossWordsException.buildInfrastructure("tech", "user msg", ExceptionType.TECHNICAL);

        adapter.presentCrossWordsException(ex, request, response);

        assertThat(writer.toString()).contains("INTERNAL_SERVER_ERROR");
    }

    @Test
    void handleGeneralException_logsError_whenWriterFails() throws Exception {
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(serializerRegistry.getSerializerForMediaType("application/json")).thenReturn(serializer);
        when(serializer.serialize(any(ErrorResponse.class))).thenReturn("json");
        when(response.getWriter()).thenThrow(new IOException("io"));
        when(catalogPort.getMessage("FUN_023")).thenReturn("fallback");
        when(catalogPort.getMessage("TCH_019")).thenReturn("wrap error");

        adapter.handleGeneralException(new RuntimeException("boom"), request, response);

        verify(log).error(eq("wrap error"), any(IOException.class));
    }
}
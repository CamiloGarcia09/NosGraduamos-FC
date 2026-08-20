package co.edu.uco.infraestructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class LoggingConfigTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    private final LoggingConfig loggingConfig = new LoggingConfig();

    @Test
    void preHandle_generatesCorrelationId_whenHeaderIsMissing() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        boolean result = loggingConfig.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(MDC.get("X-Correlation-ID")).isNotNull();
    }

    @Test
    void preHandle_reusesCorrelationId_whenHeaderIsPresent() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("existing-id");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        boolean result = loggingConfig.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        assertThat(MDC.get("X-Correlation-ID")).isEqualTo("existing-id");
    }

    @Test
    void preHandle_setsResponseHeaders() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        verify(response).setHeader(eq("X-Correlation-ID"), eq("id-1"));
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
    }

    @Test
    void preHandle_setsQueryStringInMDC_whenPresent() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn("param=value");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        assertThat(MDC.get("QUERY_STRING")).isEqualTo("param=value");
    }

    @Test
    void preHandle_skipsQueryStringInMDC_whenEmpty() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        assertThat(MDC.get("QUERY_STRING")).isNull();
    }

    @Test
    void preHandle_setsCodeMessageParameterInMDC_whenPresent() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getParameter("codeMessage")).thenReturn("MSG-001");
        lenient().when(request.getParameter("application")).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        assertThat(MDC.get("codeMessage")).isEqualTo("MSG-001");
    }

    @Test
    void preHandle_setsApplicationParameterInMDC_whenPresent() throws Exception {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getParameter("codeMessage")).thenReturn(null);
        lenient().when(request.getParameter("application")).thenReturn("my-app");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        assertThat(MDC.get("application")).isEqualTo("my-app");
    }

    @Test
    void afterCompletion_clearsMDC() throws Exception {
        MDC.put("test", "value");
        loggingConfig.afterCompletion(request, response, new Object(), null);
        assertThat(MDC.get("test")).isNull();
    }
}

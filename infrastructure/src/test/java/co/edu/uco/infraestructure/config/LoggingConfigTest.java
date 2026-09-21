package co.edu.uco.infraestructure.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
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

    private ListAppender<ILoggingEvent> attachListAppender() {
        var root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(LoggingConfig.class);
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        root.addAppender(appender);
        return appender;
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void preHandle_generatesCorrelationId_whenHeaderIsMissing() {
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
    void preHandle_reusesCorrelationId_whenHeaderIsPresent() {
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
    void preHandle_setsResponseHeaders() {
        when(request.getHeader("X-Correlation-ID")).thenReturn("id-1");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("session-1");

        loggingConfig.preHandle(request, response, new Object());

        verify(response).setHeader("X-Correlation-ID", "id-1");
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
        verify(response, atLeastOnce()).setHeader(anyString(), anyString());
    }

    @Test
    void preHandle_setsQueryStringInMDC_whenPresent() {
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
    void preHandle_skipsQueryStringInMDC_whenEmpty() {
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
    void preHandle_setsCodeMessageParameterInMDC_whenPresent() {
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
    void preHandle_setsApplicationParameterInMDC_whenPresent() {
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
    void afterCompletion_clearsMDC() {
        MDC.put("test", "value");
        loggingConfig.afterCompletion(request, response, new Object(), null);
        assertThat(MDC.get("test")).isNull();
    }

    @Test
    void afterCompletion_setsStatusAndDuration_whenStartTimePresent() {
        when(request.getAttribute("LOGGING_START_TIME")).thenReturn(System.currentTimeMillis() - 100L);
        when(response.getStatus()).thenReturn(201);
        ListAppender<ILoggingEvent> appender = attachListAppender();

        loggingConfig.afterCompletion(request, response, new Object(), null);

        assertThat(appender.list).anyMatch(event -> event.getFormattedMessage().contains("201"));
        assertThat(appender.list).allMatch(event -> event.getMDCPropertyMap().containsKey("HTTP_STATUS"));
        assertThat(appender.list).allMatch(event -> event.getMDCPropertyMap().containsKey("DURATION_MS"));
        assertThat(MDC.get("HTTP_STATUS")).isNull();
    }

    @Test
    void afterCompletion_logsStatus_whenNoStartTime() {
        when(request.getAttribute("LOGGING_START_TIME")).thenReturn(null);
        when(response.getStatus()).thenReturn(200);
        ListAppender<ILoggingEvent> appender = attachListAppender();

        loggingConfig.afterCompletion(request, response, new Object(), null);

        assertThat(appender.list).anyMatch(event -> event.getFormattedMessage().contains("200"));
        assertThat(appender.list).allMatch(event -> !event.getMDCPropertyMap().containsKey("DURATION_MS"));
        assertThat(MDC.get("HTTP_STATUS")).isNull();
    }

    @Test
    void afterCompletion_withException_stillClearsMDC() {
        when(request.getAttribute("LOGGING_START_TIME")).thenReturn(null);
        when(response.getStatus()).thenReturn(500);
        ListAppender<ILoggingEvent> appender = attachListAppender();

        loggingConfig.afterCompletion(request, response, new Object(), new RuntimeException("boom"));

        assertThat(appender.list).anyMatch(event -> event.getFormattedMessage().contains("exception"));
        assertThat(MDC.get("HTTP_STATUS")).isNull();
    }
}

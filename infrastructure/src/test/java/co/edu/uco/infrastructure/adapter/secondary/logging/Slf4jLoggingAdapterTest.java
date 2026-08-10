package co.edu.uco.infrastructure.adapter.secondary.logging;

import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

class Slf4jLoggingAdapterTest {

    @Test
    void info_delegates_to_slf4j_logger() {
        Logger mockLogger = mock(Logger.class);
        LoggingPort adapter = new Slf4jLoggingAdapter(mockLogger);

        adapter.info("test info message");

        verify(mockLogger).info("test info message");
    }

    @Test
    void warn_delegates_to_slf4j_logger() {
        Logger mockLogger = mock(Logger.class);
        LoggingPort adapter = new Slf4jLoggingAdapter(mockLogger);

        adapter.warn("test warn message");

        verify(mockLogger).warn("test warn message");
    }

    @Test
    void error_without_cause_delegates_to_slf4j_logger() {
        Logger mockLogger = mock(Logger.class);
        LoggingPort adapter = new Slf4jLoggingAdapter(mockLogger);

        adapter.error("test error message");

        verify(mockLogger).error("test error message");
    }

    @Test
    void error_with_cause_delegates_to_slf4j_logger() {
        Logger mockLogger = mock(Logger.class);
        LoggingPort adapter = new Slf4jLoggingAdapter(mockLogger);
        Throwable cause = new RuntimeException("cause");

        adapter.error("test error message", cause);

        verify(mockLogger).error("test error message", cause);
    }
}

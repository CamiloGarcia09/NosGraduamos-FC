package co.edu.uco.core.domain.port.out.logging;

public interface LoggingPort {
    void info(String message);
    void warn(String message);
    void error(String message);
    void error(String message, Throwable cause);
}

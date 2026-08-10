package co.edu.uco.core.domain.port.out.logging;

public interface LoggingPortFactory {
    LoggingPort getLogger(Class<?> source);
}

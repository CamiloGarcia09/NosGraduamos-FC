package co.edu.uco.infrastructure.adapter.secondary.logging;

import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import org.slf4j.Logger;

final class Slf4jLoggingAdapter implements LoggingPort {

    private final Logger logger;

    Slf4jLoggingAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Throwable cause) {
        logger.error(message, cause);
    }
}

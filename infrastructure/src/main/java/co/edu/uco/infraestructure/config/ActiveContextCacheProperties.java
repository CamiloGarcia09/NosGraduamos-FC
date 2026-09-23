package co.edu.uco.infraestructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "components.context.cache")
public class ActiveContextCacheProperties {

    private long ttlSeconds = 900L;

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(final long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}

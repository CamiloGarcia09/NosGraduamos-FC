package co.edu.uco.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.JPA_CONFIG_PREFIX;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = JPA_CONFIG_PREFIX)
public class DatabaseProperties {
    private String url;
    private String username;
    private String password;
    private String dialect;
    private boolean showSql;
}
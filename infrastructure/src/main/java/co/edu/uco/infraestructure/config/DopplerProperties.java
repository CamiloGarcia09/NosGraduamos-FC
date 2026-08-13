package co.edu.uco.infraestructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.DOPPLER_CONFIG_PREFIX;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = DOPPLER_CONFIG_PREFIX)
public class DopplerProperties {
    private String token;
    private String request;
    private String urlConfigSecretsPost;
    private String urlConfigSecretsGet;
}
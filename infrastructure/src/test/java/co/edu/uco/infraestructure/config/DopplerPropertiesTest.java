package co.edu.uco.infraestructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DopplerPropertiesTest {

    @Test
    void gettersAndSetters_roundTripValues() {
        DopplerProperties properties = new DopplerProperties();
        properties.setToken("token");
        properties.setRequest("request");
        properties.setUrlConfigSecretsPost("post");
        properties.setUrlConfigSecretsGet("get");

        assertThat(properties.getToken()).isEqualTo("token");
        assertThat(properties.getRequest()).isEqualTo("request");
        assertThat(properties.getUrlConfigSecretsPost()).isEqualTo("post");
        assertThat(properties.getUrlConfigSecretsGet()).isEqualTo("get");
    }
}
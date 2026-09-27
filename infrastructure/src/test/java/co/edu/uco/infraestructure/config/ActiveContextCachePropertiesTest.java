package co.edu.uco.infraestructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveContextCachePropertiesTest {

    @Test
    void constructor_usesDefaultTtl() {
        assertThat(new ActiveContextCacheProperties().getTtlSeconds()).isEqualTo(900L);
    }

    @Test
    void setter_overridesTtl() {
        ActiveContextCacheProperties properties = new ActiveContextCacheProperties();

        properties.setTtlSeconds(45L);

        assertThat(properties.getTtlSeconds()).isEqualTo(45L);
    }
}

package co.edu.uco.infraestructure.secondaryadapters.repository.redis;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageRedisTest {

    @Test
    void defaultConstructor_initializesEmptyFields() {
        MessageRedis redis = new MessageRedis();

        assertThat(redis.getId()).isNotNull();
        assertThat(redis.getCode()).isEmpty();
        assertThat(redis.getTitle()).isEmpty();
        assertThat(redis.getContent()).isEmpty();
        assertThat(redis.getType()).isEmpty();
        assertThat(redis.getCategory()).isEmpty();
        assertThat(redis.getStatus()).isEmpty();
        assertThat(redis.getApplication()).isEmpty();
        assertThat(redis.getFunctionality()).isEmpty();
        assertThat(redis.getEnvironmentId()).isEmpty();
    }

    @Test
    void fullConstructor_assignsAndTrimsValues() {
        UUID id = UUID.randomUUID();
        MessageRedis redis = new MessageRedis(id, " CODE ", " Title ", " Content ", " CAT ", " TYPE ",
                " STATUS ", " APP ", " FUNC ", " ENV ");

        assertThat(redis.getId()).isEqualTo(id);
        assertThat(redis.getCode()).isEqualTo("CODE");
        assertThat(redis.getTitle()).isEqualTo("Title");
        assertThat(redis.getContent()).isEqualTo("Content");
        assertThat(redis.getType()).isEqualTo("TYPE");
        assertThat(redis.getCategory()).isEqualTo("CAT");
        assertThat(redis.getStatus()).isEqualTo("STATUS");
        assertThat(redis.getApplication()).isEqualTo("APP");
        assertThat(redis.getFunctionality()).isEqualTo("FUNC");
        assertThat(redis.getEnvironmentId()).isEqualTo("ENV");
    }

    @Test
    void setters_applyTrimAndDefaultId() {
        MessageRedis redis = new MessageRedis();
        redis.setCode("  newcode  ");
        redis.setEnvironmentId(null);
        redis.setId(null);

        assertThat(redis.getCode()).isEqualTo("newcode");
        assertThat(redis.getEnvironmentId()).isEmpty();
        assertThat(redis.getId()).isNotNull();
    }
}
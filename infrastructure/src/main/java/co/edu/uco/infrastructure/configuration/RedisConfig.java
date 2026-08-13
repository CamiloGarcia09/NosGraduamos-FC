package co.edu.uco.infrastructure.configuration;

import co.edu.uco.infrastructure.adapter.secondary.repository.redis.MessageRedis;
import co.edu.uco.utils.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PACKAGE_REPOSITORY_ADAPTER;

@Slf4j
@Configuration
@EnableRedisRepositories(basePackages = {PACKAGE_REPOSITORY_ADAPTER})
public class RedisConfig {
    @Bean
    public RedisTemplate<String, MessageRedis> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MessageRedis> template = new RedisTemplate<>();
        try {
            template.setConnectionFactory(connectionFactory);
            var objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(MessageRedis.class)
                    .build();
            objectMapper.activateDefaultTyping(
                    ptv,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
            );
            Jackson2JsonRedisSerializer<MessageRedis> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, MessageRedis.class);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(serializer);
            template.afterPropertiesSet();
        } catch (RedisConnectionFailureException ex) {
            log.error("FUN_013", ex);
            throw BusinessException.buildTechnicalException("FUN_013");
        } catch (DataAccessException ex) {
            log.error("FUN_014", ex);
            throw BusinessException.buildTechnicalException("FUN_014");
        } catch (Exception ex) {
            log.error("FUN_015", ex);
            throw BusinessException.buildTechnicalException("FUN_015");
        }
        return template;
    }
}
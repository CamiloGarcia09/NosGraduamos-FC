package co.edu.uco.infraestructure.secondaryadapters.cache;

import co.edu.uco.application.secondaryports.cache.ActiveContextCachePort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.infraestructure.config.ActiveContextCacheProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;

@Component
public final class ActiveContextRedisAdapter implements ActiveContextCachePort {

    private static final String KEY_PREFIX = "messageucolab:active-context:v1:";
    private static final String READ_ERROR = "Active context cache read failed";
    private static final String WRITE_ERROR = "Active context cache write failed";
    private static final String EVICT_ERROR = "Active context cache eviction failed";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ActiveContextCacheProperties properties;
    private final LoggingPort log;

    public ActiveContextRedisAdapter(final StringRedisTemplate redisTemplate,
                                     final ObjectMapper objectMapper,
                                     final ActiveContextCacheProperties properties,
                                     final LoggingPortFactory loggerFactory) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.log = loggerFactory.getLogger(ActiveContextRedisAdapter.class);
    }

    @Override
    public Optional<ActiveContextEntity> find(final ExternalIdentity identity) {
        try {
            final Optional<String> key = keyFor(identity);
            if (key.isEmpty()) {
                return Optional.empty();
            }
            final String json = redisTemplate.opsForValue().get(key.get());
            if (json == null || json.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(toEntity(objectMapper.readValue(json, ActiveContextCacheModel.class)));
        } catch (final JsonProcessingException | RuntimeException exception) {
            log.warn(READ_ERROR);
            return Optional.empty();
        }
    }

    @Override
    public void save(final ExternalIdentity identity, final ActiveContextEntity context) {
        try {
            final Optional<String> key = keyFor(identity);
            if (key.isEmpty() || context == null) {
                return;
            }
            final ActiveContextCacheModel model = fromEntity(context);
            redisTemplate.opsForValue().set(
                    key.get(), objectMapper.writeValueAsString(model), Duration.ofSeconds(properties.getTtlSeconds()));
        } catch (final JsonProcessingException | RuntimeException exception) {
            log.warn(WRITE_ERROR);
        }
    }

    @Override
    public void evict(final ExternalIdentity identity) {
        try {
            final Optional<String> key = keyFor(identity);
            if (key.isEmpty()) {
                return;
            }
            redisTemplate.delete(key.get());
        } catch (final RuntimeException exception) {
            log.warn(EVICT_ERROR);
        }
    }

    private Optional<String> keyFor(final ExternalIdentity identity) {
        if (identity == null || isBlank(identity.issuer()) || isBlank(identity.subject())) {
            return Optional.empty();
        }
        final byte[] issuer = identity.issuer().trim().getBytes(StandardCharsets.UTF_8);
        final byte[] subject = identity.subject().trim().getBytes(StandardCharsets.UTF_8);
        final ByteBuffer keyMaterial = ByteBuffer.allocate(Integer.BYTES * 2 + issuer.length + subject.length);
        keyMaterial.putInt(issuer.length).put(issuer).putInt(subject.length).put(subject);
        return Optional.of(KEY_PREFIX + HexFormat.of().formatHex(sha256(keyMaterial.array())));
    }

    private byte[] sha256(final byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable");
        }
    }

    private ActiveContextCacheModel fromEntity(final ActiveContextEntity context) {
        return new ActiveContextCacheModel(
                required(context.getId()).toString(),
                required(context.getExternalIdentityId()).toString(),
                required(context.getOrganizationId()).toString(),
                required(context.getApplicationId()).toString(),
                required(context.getEnvironmentId()).toString(),
                required(context.getUpdatedAt()).toString());
    }

    private ActiveContextEntity toEntity(final ActiveContextCacheModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Invalid active context cache entry");
        }
        final ActiveContextEntity context = new ActiveContextEntity();
        context.setId(parseUuid(model.getId()));
        context.setExternalIdentityId(parseUuid(model.getExternalIdentityId()));
        context.setOrganizationId(parseUuid(model.getOrganizationId()));
        context.setApplicationId(parseUuid(model.getApplicationId()));
        context.setEnvironmentId(parseUuid(model.getEnvironmentId()));
        context.setUpdatedAt(LocalDateTime.parse(requiredText(model.getUpdatedAt())));
        return context;
    }

    private UUID parseUuid(final String value) {
        final UUID parsed = UUID.fromString(requiredText(value));
        if (DEFAULT_UUID.equals(parsed)) {
            throw new IllegalArgumentException("Missing active context cache field");
        }
        return parsed;
    }

    private String requiredText(final String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Missing active context cache field");
        }
        return value.trim();
    }

    private <T> T required(final T value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing active context cache field");
        }
        return value;
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}

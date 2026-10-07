package co.edu.uco.infraestructure.secondaryadapters.cache;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.infraestructure.config.ActiveContextCacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActiveContextRedisAdapterTest {

    private static final String PREFIX = "messageucolab:active-context:v1:";

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private LoggingPortFactory loggerFactory;
    @Mock private LoggingPort log;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ActiveContextRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        ActiveContextCacheProperties properties = new ActiveContextCacheProperties();
        properties.setTtlSeconds(321L);
        when(loggerFactory.getLogger(ActiveContextRedisAdapter.class)).thenReturn(log);
        adapter = new ActiveContextRedisAdapter(redisTemplate, objectMapper, properties, loggerFactory);
    }

    @Test
    void find_returnsMappedContext_onCacheHit() throws Exception {
        ActiveContextEntity expected = context();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(json(expected));

        Optional<ActiveContextEntity> result = adapter.find(identity("issuer", "subject"));

        assertThat(result).hasValueSatisfying(actual -> assertThat(actual).satisfies(context -> {
            assertThat(context.getId()).isEqualTo(expected.getId());
            assertThat(context.getExternalIdentityId()).isEqualTo(expected.getExternalIdentityId());
            assertThat(context.getOrganizationId()).isEqualTo(expected.getOrganizationId());
            assertThat(context.getApplicationId()).isEqualTo(expected.getApplicationId());
            assertThat(context.getEnvironmentId()).isEqualTo(expected.getEnvironmentId());
            assertThat(context.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
        }));
        verifyNoInteractions(log);
    }

    @Test
    void find_returnsMiss_whenRedisHasNoValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        assertThat(adapter.find(identity("issuer", "subject"))).isEmpty();
    }

    @Test
    void find_returnsMiss_whenRedisHasBlankValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("  ");

        assertThat(adapter.find(identity("issuer", "subject"))).isEmpty();
    }

    @Test
    void find_rejectsMalformedJsonWithSanitizedLog() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("{secret-json");

        assertThat(adapter.find(identity("private-issuer", "private-subject"))).isEmpty();
        verify(log).warn("Active context cache read failed");
    }

    @Test
    void find_rejectsEntryWhenAnyRequiredFieldIsMissing() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("{\"id\":\"" + UUID.randomUUID()
                + "\",\"externalIdentityId\":\"" + UUID.randomUUID()
                + "\",\"organizationId\":\"" + UUID.randomUUID()
                + "\",\"applicationId\":\"" + UUID.randomUUID()
                + "\",\"updatedAt\":\"2026-09-22T10:15:30\"}");

        assertThat(adapter.find(identity("issuer", "subject"))).isEmpty();
        verify(log).warn("Active context cache read failed");
    }

    @Test
    void find_rejectsInvalidUuidAndTimestamp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("{\"id\":\"not-a-uuid\","
                + "\"externalIdentityId\":\"not-a-uuid\",\"organizationId\":\"not-a-uuid\","
                + "\"applicationId\":\"not-a-uuid\",\"environmentId\":\"not-a-uuid\","
                + "\"updatedAt\":\"not-a-time\"}");

        assertThat(adapter.find(identity("issuer", "subject"))).isEmpty();
    }

    @Test
    void find_rejectsDefaultUuidAsMissingRequiredData() {
        String defaultId = "00000000-0000-0000-0000-000000000000";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("{\"id\":\"" + defaultId
                + "\",\"externalIdentityId\":\"" + UUID.randomUUID()
                + "\",\"organizationId\":\"" + UUID.randomUUID()
                + "\",\"applicationId\":\"" + UUID.randomUUID()
                + "\",\"environmentId\":\"" + UUID.randomUUID()
                + "\",\"updatedAt\":\"2026-09-22T10:15:30\"}");

        assertThat(adapter.find(identity("issuer", "subject"))).isEmpty();
    }

    @Test
    void find_treatsRedisFailureAsMissAndUsesSanitizedLog() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenThrow(new RuntimeException("key and identity leaked"));

        assertThat(adapter.find(identity("issuer-secret", "subject-secret"))).isEmpty();
        verify(log).warn("Active context cache read failed");
    }

    @Test
    void save_serializesContextAndAppliesConfiguredTtl() throws Exception {
        ActiveContextEntity context = context();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        adapter.save(identity("issuer", "subject"), context);

        ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(key.capture(), json.capture(), eq(Duration.ofSeconds(321)));
        assertThat(key.getValue()).matches("^" + PREFIX + "[0-9a-f]{64}$");
        ActiveContextCacheModel saved = objectMapper.readValue(json.getValue(), ActiveContextCacheModel.class);
        assertThat(saved.getId()).isEqualTo(context.getId().toString());
        assertThat(saved.getEnvironmentId()).isEqualTo(context.getEnvironmentId().toString());
        assertThat(json.getValue()).doesNotContain("issuer", "subject", "mail@example.com");
    }

    @Test
    void save_isBestEffort_whenSerializationDataIsIncomplete() {
        ActiveContextEntity incomplete = new ActiveContextEntity();

        adapter.save(identity("issuer", "subject"), incomplete);

        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        verify(log).warn("Active context cache write failed");
    }

    @Test
    void save_isBestEffort_whenRedisFails() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("generated key leaked"))
                .when(valueOperations).set(anyString(), anyString(), any(Duration.class));

        adapter.save(identity("issuer-secret", "subject-secret"), context());

        verify(log).warn("Active context cache write failed");
    }

    @Test
    void evict_deletesTheSameKeyUsedByFind() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        ExternalIdentity identity = identity(" issuer ", " subject ");

        adapter.find(identity);
        adapter.evict(identity);

        ArgumentCaptor<String> readKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> evictedKey = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).get(readKey.capture());
        verify(redisTemplate).delete(evictedKey.capture());
        assertThat(evictedKey.getValue()).isEqualTo(readKey.getValue());
    }

    @Test
    void evict_isBestEffort_whenRedisFails() {
        when(redisTemplate.delete(anyString())).thenThrow(new RuntimeException("generated key leaked"));

        adapter.evict(identity("issuer-secret", "subject-secret"));

        verify(log).warn("Active context cache eviction failed");
    }

    @Test
    void key_isStableForTrimmedIdentityAndContainsNoIdentityData() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        ExternalIdentity first = identity(" private-issuer ", " private-subject ");
        ExternalIdentity second = identity("private-issuer", "private-subject");

        adapter.find(first);
        adapter.find(second);

        ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(2)).get(keys.capture());
        assertThat(keys.getAllValues()).containsOnly(keys.getAllValues().get(0));
        assertThat(keys.getAllValues().get(0))
                .startsWith(PREFIX)
                .doesNotContain("private-issuer", "private-subject", "mail@example.com");
    }

    @Test
    void lengthPrefixingPreventsConcatenationCollisions() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        adapter.find(identity("ab", "c"));
        adapter.find(identity("a", "bc"));

        ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(2)).get(keys.capture());
        assertThat(keys.getAllValues().get(0)).isNotEqualTo(keys.getAllValues().get(1));
    }

    @Test
    void nullOrBlankIdentity_isAlwaysNoOp() {
        assertThat(adapter.find(null)).isEmpty();
        assertThat(adapter.find(identity(" ", "subject"))).isEmpty();
        assertThat(adapter.find(identity("issuer", null))).isEmpty();
        adapter.save(null, context());
        adapter.save(identity("issuer", ""), context());
        adapter.evict(identity(null, "subject"));

        verifyNoInteractions(redisTemplate, log);
    }

    private ExternalIdentity identity(final String issuer, final String subject) {
        return new ExternalIdentity(issuer, subject, "mail@example.com", Instant.MAX);
    }

    private ActiveContextEntity context() {
        ActiveContextEntity context = new ActiveContextEntity();
        context.setId(UUID.randomUUID());
        context.setExternalIdentityId(UUID.randomUUID());
        context.setOrganizationId(UUID.randomUUID());
        context.setApplicationId(UUID.randomUUID());
        context.setEnvironmentId(UUID.randomUUID());
        context.setUpdatedAt(LocalDateTime.of(2026, 9, 22, 10, 15, 30));
        return context;
    }

    private String json(final ActiveContextEntity context) throws Exception {
        return objectMapper.writeValueAsString(new ActiveContextCacheModel(
                context.getId().toString(),
                context.getExternalIdentityId().toString(),
                context.getOrganizationId().toString(),
                context.getApplicationId().toString(),
                context.getEnvironmentId().toString(),
                context.getUpdatedAt().toString()));
    }
}

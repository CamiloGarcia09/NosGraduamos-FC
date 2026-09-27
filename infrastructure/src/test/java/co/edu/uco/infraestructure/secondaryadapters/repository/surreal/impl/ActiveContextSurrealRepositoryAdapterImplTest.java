package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.ActiveContextSurrealMapper;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActiveContextSurrealRepositoryAdapterImplTest {

    @Mock private Surreal surreal;
    @Mock private LoggingPortFactory loggerFactory;
    @Mock private LoggingPort log;
    private ActiveContextSurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(ActiveContextSurrealRepositoryAdapterImpl.class)).thenReturn(log);
        adapter = new ActiveContextSurrealRepositoryAdapterImpl(
                surreal, loggerFactory, new ActiveContextSurrealMapper());
    }

    @Test
    void findByExternalIdentityId_usesDeterministicRecordIdAndMapsReferences() {
        ActiveContextEntity expected = context();
        doReturn(responseWith(document(expected))).when(surreal).query(anyString());

        Optional<ActiveContextEntity> result = adapter.findByExternalIdentityId(expected.getExternalIdentityId());

        assertThat(result).hasValueSatisfying(actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getExternalIdentityId()).isEqualTo(expected.getExternalIdentityId());
            assertThat(actual.getOrganizationId()).isEqualTo(expected.getOrganizationId());
            assertThat(actual.getApplicationId()).isEqualTo(expected.getApplicationId());
            assertThat(actual.getEnvironmentId()).isEqualTo(expected.getEnvironmentId());
            assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
        });
        verifyQuery("SELECT * FROM active_context:`" + expected.getExternalIdentityId() + "` LIMIT 1;");
        verifyNoInteractions(log);
    }

    @Test
    void findByExternalIdentityId_returnsEmpty_whenNoRowExists() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        doReturn(response).when(surreal).query(anyString());

        assertThat(adapter.findByExternalIdentityId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void findByExternalIdentityId_wrapsFailureAndLogsOnlyGenericMessage() {
        RuntimeException failure = new RuntimeException("SELECT secret-id");
        doThrow(failure).when(surreal).query(anyString());
        UUID externalIdentityId = UUID.randomUUID();

        assertThatThrownBy(() -> adapter.findByExternalIdentityId(externalIdentityId))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Unable to read active context from persistence"));
        verify(log).error("Active context persistence read failed");
    }

    @Test
    void save_upsertsOneDeterministicRowWithRecordReferencesAndTimestamp() {
        ActiveContextEntity context = context();

        adapter.save(context);

        verifyQuery("UPSERT active_context:`" + context.getExternalIdentityId() + "` CONTENT { "
                + "external_identity_id: external_identity:`" + context.getExternalIdentityId() + "`, "
                + "organization_id: organization:`" + context.getOrganizationId() + "`, "
                + "application_id: application:`" + context.getApplicationId() + "`, "
                + "environment_id: environment:`" + context.getEnvironmentId() + "`, "
                + "updated_at: d'2026-09-22T10:15:30Z' };" );
        verifyNoInteractions(log);
    }

    @Test
    void save_wrapsFailureAndDoesNotLogSqlOrIdentifiers() {
        ActiveContextEntity context = context();
        doThrow(new RuntimeException("query and id leaked by driver")).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.save(context))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Unable to write active context to persistence"));
        verify(log).error("Active context persistence write failed");
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

    private Object document(final ActiveContextEntity context) {
        Object document = mock(Object.class);
        doReturn(recordValue("active_context", context.getId())).when(document).get("id");
        doReturn(recordValue("external_identity", context.getExternalIdentityId()))
                .when(document).get("external_identity_id");
        doReturn(recordValue("organization", context.getOrganizationId())).when(document).get("organization_id");
        doReturn(recordValue("application", context.getApplicationId())).when(document).get("application_id");
        doReturn(recordValue("environment", context.getEnvironmentId())).when(document).get("environment_id");
        Value timestamp = mock(Value.class);
        when(timestamp.isDateTime()).thenReturn(true);
        when(timestamp.getDateTime()).thenReturn(ZonedDateTime.of(context.getUpdatedAt(), ZoneOffset.UTC));
        doReturn(timestamp).when(document).get("updated_at");
        return document;
    }

    private Value recordValue(final String table, final UUID id) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn(table + ":" + id);
        Value value = mock(Value.class);
        when(value.isRecordId()).thenReturn(true);
        when(value.getRecordId()).thenReturn(recordId);
        return value;
    }

    private Response responseWith(final Object document) {
        Response response = mock(Response.class);
        Value statement = mock(Value.class);
        Array array = mock(Array.class);
        Value item = mock(Value.class);
        when(response.size()).thenReturn(1);
        when(response.take(0)).thenReturn(statement);
        when(statement.isArray()).thenReturn(true);
        when(statement.getArray()).thenReturn(array);
        when(array.len()).thenReturn(1);
        when(array.get(0)).thenReturn(item);
        when(item.isObject()).thenReturn(true);
        when(item.getObject()).thenReturn(document);
        return response;
    }

    private void verifyQuery(final String expected) {
        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        verify(surreal).query(query.capture());
        assertThat(query.getValue()).isEqualTo(expected);
    }
}

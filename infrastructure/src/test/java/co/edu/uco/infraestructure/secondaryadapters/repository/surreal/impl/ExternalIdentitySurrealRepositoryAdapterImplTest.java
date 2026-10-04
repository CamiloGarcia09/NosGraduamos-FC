package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.ExternalIdentitySurrealMapper;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalIdentitySurrealRepositoryAdapterImplTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private ExternalIdentitySurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(ExternalIdentitySurrealRepositoryAdapterImpl.class)).thenReturn(log);
        adapter = new ExternalIdentitySurrealRepositoryAdapterImpl(
                surreal, loggerFactory, new ExternalIdentitySurrealMapper());
    }

    @Test
    void findByIssuerAndSubject_returnsIdentity_whenDocumentExists() {
        UUID id = UUID.randomUUID();
        doReturn(responseWithOne(externalIdentityDocument(id, "https://google.com", "u1",
                "user@google.com")))
                .when(surreal).query(anyString());

        Optional<ExternalIdentityEntity> result = adapter.findByIssuerAndSubject("https://google.com", "u1");

        assertThat(result).hasValueSatisfying(entity -> assertThat(entity).satisfies(e -> {
            assertThat(e.getId()).isEqualTo(id);
            assertThat(e.getIssuer()).isEqualTo("https://google.com");
            assertThat(e.getSubject()).isEqualTo("u1");
            assertThat(e.getEmail()).isEqualTo("user@google.com");
        }));
        verifyQuery("SELECT * FROM external_identity WHERE issuer = 'https://google.com' AND subject = 'u1' LIMIT 1;");
    }

    @Test
    void findByIssuerAndSubject_returnsEmpty_whenDocumentDoesNotExist() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        Optional<ExternalIdentityEntity> result = adapter.findByIssuerAndSubject("https://google.com", "u1");

        assertThat(result).isEmpty();
    }

    @Test
    void findByIssuerAndSubject_throwsBusinessException_whenQueryFails() {
        RuntimeException cause = new RuntimeException("connection lost");
        doThrow(cause).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.findByIssuerAndSubject("issuer", "subject"))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Error al consultar la identidad externa en la base de datos SurrealDB"));
        verify(log).error("Error al consultar identidad externa en SurrealDB", cause);
    }

    @Test
    void create_persistsExternalIdentity_withoutLoggingSensitiveData() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = externalIdentity(id, "https://google.com", "u1",
                "user@google.com");

        adapter.create(entity);

        verifyQuery("UPSERT external_identity:`" + id
                + "` CONTENT { issuer: 'https://google.com', subject: 'u1', email: 'user@google.com' };");
        verify(log).info("Executing SurrealQL create external identity");
        verifyNoMoreInteractions(log);
    }

    @Test
    void create_escapesSlashInIssuer() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = externalIdentity(id, "https://auth.example.com", "u1",
                null);

        adapter.create(entity);

        verifyQuery("UPSERT external_identity:`" + id
                + "` CONTENT { issuer: 'https://auth.example.com', subject: 'u1', email: NONE };");
    }

    @Test
    void create_escapesQuoteInSubject() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = externalIdentity(id, "issuer", "it's me",
                null);

        adapter.create(entity);

        verifyQuery("UPSERT external_identity:`" + id
                + "` CONTENT { issuer: 'issuer', subject: 'it\\'s me', email: NONE };");
    }

    @Test
    void create_throwsBusinessException_whenQueryFails() {
        RuntimeException cause = new RuntimeException("db down");
        ExternalIdentityEntity entity = externalIdentity(UUID.randomUUID(), "issuer", "subject",
                null);
        doThrow(cause).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.create(entity))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Error al crear la identidad externa en la base de datos SurrealDB"));
        verify(log).error("Error al crear la identidad externa en SurrealDB", cause);
    }

    @Test
    void update_mergesEmailWithoutPrincipalType_withoutLoggingIdentifierFields() {
        UUID id = UUID.randomUUID();
        ExternalIdentityEntity entity = externalIdentity(id, "issuer", "subject",
                "new@email.com");

        adapter.update(entity);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(surreal).query(captor.capture());
        String sql = captor.getValue();
        assertThat(sql)
                .contains("email: 'new@email.com'")
                .contains("time::now()")
                .doesNotContain("principal_type")
                .doesNotContain("issuer:")
                .doesNotContain("subject:");
        verify(log).info("Executing SurrealQL update external identity");
    }

    @Test
    void update_throwsBusinessException_whenQueryFails() {
        RuntimeException cause = new RuntimeException("db down");
        ExternalIdentityEntity entity = externalIdentity(UUID.randomUUID(), "issuer", "subject",
                null);
        doThrow(cause).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.update(entity))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getTechnicalMessage())
                        .isEqualTo("Error al actualizar la identidad externa en la base de datos SurrealDB"));
        verify(log).error("Error al actualizar la identidad externa en SurrealDB", cause);
    }

    private ExternalIdentityEntity externalIdentity(final UUID id, final String issuer,
                                                     final String subject, final String email) {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(id);
        entity.setIssuer(issuer);
        entity.setSubject(subject);
        entity.setEmail(email);
        return entity;
    }

    private Object externalIdentityDocument(final UUID id, final String issuer,
                                             final String subject, final String email) {
        Object document = mock(Object.class);
        doReturn(recordIdValue(id)).when(document).get("id");
        doReturn(stringValue(issuer)).when(document).get("issuer");
        doReturn(stringValue(subject)).when(document).get("subject");
        doReturn(stringValue(email)).when(document).get("email");
        return document;
    }

    private Value recordIdValue(final UUID id) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn("external_identity:" + id);
        Value value = mock(Value.class);
        when(value.isRecordId()).thenReturn(true);
        when(value.getRecordId()).thenReturn(recordId);
        return value;
    }

    private Value stringValue(final String text) {
        Value value = mock(Value.class);
        when(value.isNull()).thenReturn(false);
        when(value.isNone()).thenReturn(false);
        when(value.isString()).thenReturn(true);
        when(value.getString()).thenReturn(text);
        return value;
    }

    private Response responseWithOne(final Object document) {
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

    private Response emptyResponse() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        return response;
    }

    private void verifyQuery(final String expectedSql) {
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(surreal).query(queryCaptor.capture());
        assertThat(queryCaptor.getValue()).isEqualTo(expectedSql);
    }
}

package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationSurrealRepositoryAdapterImplTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private ApplicationSurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(ApplicationSurrealRepositoryAdapterImpl.class)).thenReturn(log);
        adapter = new ApplicationSurrealRepositoryAdapterImpl(surreal, loggerFactory);
    }

    private Value stringValue(String value) {
        Value v = mock(Value.class);
        when(v.isNull()).thenReturn(false);
        when(v.isNone()).thenReturn(false);
        when(v.isString()).thenReturn(true);
        when(v.getString()).thenReturn(value);
        return v;
    }

    private Value recordIdValue(String table, String uuid) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn(table + ":" + uuid);
        Value v = mock(Value.class);
        when(v.isRecordId()).thenReturn(true);
        when(v.getRecordId()).thenReturn(recordId);
        return v;
    }

    private Object applicationDocument(String uuid, String name) {
        Object doc = mock(Object.class);
        doReturn(recordIdValue("application", uuid)).when(doc).get("id");
        doReturn(stringValue(name)).when(doc).get("name");
        return doc;
    }

    private Response responseWithOne(Object document) {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(true);
        Array array = mock(Array.class);
        when(array.len()).thenReturn(1);
        Value item = mock(Value.class);
        when(item.isObject()).thenReturn(true);
        when(item.getObject()).thenReturn(document);
        when(array.get(0)).thenReturn(item);
        when(statement.getArray()).thenReturn(array);
        when(response.take(0)).thenReturn(statement);
        return response;
    }

    private Response emptyResponse() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        return response;
    }

    @Test
    void findByName_returnsApplication_whenDocumentFound() {
        String uuid = UUID.randomUUID().toString();
        doReturn(responseWithOne(applicationDocument(uuid, "App"))).when(surreal).query(anyString());

        Optional<ApplicationData> result = adapter.findByName("App");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("App");
        assertThat(result.get().getId()).isEqualTo(UUID.fromString(uuid));
    }

    @Test
    void findByName_returnsEmpty_whenResponseEmpty() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        assertThat(adapter.findByName("App")).isEmpty();
    }

    @Test
    void existsById_returnsTrue_whenDocumentFound() {
        doReturn(responseWithOne(mock(Object.class))).when(surreal).query(anyString());

        assertThat(adapter.existsById("id-1")).isTrue();
    }

    @Test
    void existsById_returnsFalse_whenResponseEmpty() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        assertThat(adapter.existsById("id-1")).isFalse();
    }

    @Test
    void create_persistsApplication() {
        ApplicationData application = ApplicationData.build();
        application.setName("App");

        adapter.create(application, "lang-1", LocalDateTime.now(), LocalDateTime.now(), "state-1");

        verify(surreal).query(anyString());
        verify(log).info(anyString(), anyString());
    }

    @Test
    void create_throwsBusinessException_whenQueryFails() {
        ApplicationData application = ApplicationData.build();
        doThrow(new RuntimeException("db down")).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.create(application, "lang-1", LocalDateTime.now(), LocalDateTime.now(), "state-1"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("Error al persistir la aplicación en la base de datos SurrealDB"));
        verify(log).error(anyString(), any(RuntimeException.class));
    }
}
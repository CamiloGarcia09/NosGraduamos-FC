package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import com.surrealdb.Array;
import com.surrealdb.Id;
import com.surrealdb.Object;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvironmentSurrealRepositoryAdapterImplTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private CatalogPort catalogPort;

    private EnvironmentSurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(EnvironmentSurrealRepositoryAdapterImpl.class)).thenReturn(log);
        when(catalogPort.getMessage(org.mockito.ArgumentMatchers.anyString())).thenReturn("msg");
        CatalogPortStaticRef.set(catalogPort);
        adapter = new EnvironmentSurrealRepositoryAdapterImpl(surreal, loggerFactory);
    }

    @AfterEach
    void tearDown() {
        CatalogPortStaticRef.set(null);
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

    private Object environmentDocument(String envUuid, String appUuid) {
        Object doc = mock(Object.class);
        doReturn(recordIdValue("environment", envUuid)).when(doc).get("id");
        doReturn(stringValue("PROD")).when(doc).get("name");
        doReturn(recordIdValue("application", appUuid)).when(doc).get("application_id");
        return doc;
    }

    @Test
    void findById_returnsEnvironment_whenFound() {
        String envUuid = UUID.randomUUID().toString();
        String appUuid = UUID.randomUUID().toString();
        doReturn(responseWithOne(environmentDocument(envUuid, appUuid))).when(surreal).query(anyString());

        Optional<EnvironmentData> result = adapter.findById(envUuid);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PROD");
        assertThat(result.get().getId()).isEqualTo(UUID.fromString(envUuid));
        assertThat(result.get().getApplication().getId()).isEqualTo(UUID.fromString(appUuid));
    }

    @Test
    void findById_returnsEnvironmentWithNullApplicationId() {
        String envUuid = UUID.randomUUID().toString();
        Object doc = mock(Object.class);
        doReturn(recordIdValue("environment", envUuid)).when(doc).get("id");
        doReturn(stringValue("DEV")).when(doc).get("name");
        when(doc.get("application_id")).thenReturn(null);
        doReturn(responseWithOne(doc)).when(surreal).query(anyString());

        Optional<EnvironmentData> result = adapter.findById(envUuid);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("DEV");
        assertThat(result.get().getApplication()).isNotNull();
    }

    @Test
    void findById_returnsEmpty_whenResponseNull() {
        when(surreal.query(anyString())).thenReturn(null);

        assertThat(adapter.findById("env-1")).isEmpty();
    }

    @Test
    void findById_returnsEmpty_whenResponseSizeZero() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findById("env-1")).isEmpty();
    }

    @Test
    void findById_returnsEmpty_whenStatementNotArray() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(false);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findById("env-1")).isEmpty();
    }

    @Test
    void findById_returnsEmpty_whenArrayEmpty() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(true);
        Array array = mock(Array.class);
        when(array.len()).thenReturn(0);
        when(statement.getArray()).thenReturn(array);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findById("env-1")).isEmpty();
    }

    @Test
    void findById_returnsEmpty_whenFirstNotObject() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(true);
        Array array = mock(Array.class);
        when(array.len()).thenReturn(1);
        Value item = mock(Value.class);
        when(item.isObject()).thenReturn(false);
        when(array.get(0)).thenReturn(item);
        when(statement.getArray()).thenReturn(array);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findById("env-1")).isEmpty();
    }

    @Test
    void findById_throwsAndLogs_whenQueryFails() {
        when(surreal.query(anyString())).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> adapter.findById("env-1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");
        verify(log).error(anyString(), any(RuntimeException.class));
    }
}
package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenStateSurrealRepositoryAdapterImplTest {

    private static final UUID DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private TokenStateSurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(TokenStateSurrealRepositoryAdapterImpl.class)).thenReturn(log);
        adapter = new TokenStateSurrealRepositoryAdapterImpl(surreal, loggerFactory);
    }

    private Value stringValue(String value) {
        Value v = mock(Value.class);
        when(v.isNull()).thenReturn(false);
        when(v.isNone()).thenReturn(false);
        when(v.isString()).thenReturn(true);
        when(v.getString()).thenReturn(value);
        return v;
    }

    private Object statusDocument(Value idValue, String name) {
        Object doc = mock(Object.class);
        when(doc.get("id")).thenReturn(idValue);
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

    private Value uuidValue(UUID uuid) {
        Value v = mock(Value.class);
        when(v.isNull()).thenReturn(false);
        when(v.isNone()).thenReturn(false);
        when(v.isUuid()).thenReturn(true);
        when(v.getUuid()).thenReturn(uuid);
        return v;
    }

    private Value recordIdValue(String table, UUID uuid) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn(table + ":" + uuid);
        Value v = mock(Value.class);
        when(v.isNull()).thenReturn(false);
        when(v.isNone()).thenReturn(false);
        when(v.isRecordId()).thenReturn(true);
        when(v.getRecordId()).thenReturn(recordId);
        return v;
    }

    @Test
    void findStatusTokenSurrealModelById_returnsModelWithUuidId() {
        UUID id = UUID.randomUUID();
        doReturn(responseWithOne(statusDocument(uuidValue(id), "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void findStatusTokenSurrealModelById_parsesRecordId() {
        UUID id = UUID.randomUUID();
        doReturn(responseWithOne(statusDocument(recordIdValue("token_state", id), "INACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("INACTIVE");
    }

    @Test
    void findStatusTokenSurrealModelById_parsesStringId() {
        UUID id = UUID.randomUUID();
        doReturn(responseWithOne(statusDocument(stringValue(id.toString()), "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultUuid_whenInvalid() {
        Value invalid = mock(Value.class);
        when(invalid.isNull()).thenReturn(false);
        when(invalid.isNone()).thenReturn(false);
        when(invalid.toString()).thenReturn("not-a-uuid");
        doReturn(responseWithOne(statusDocument(invalid, "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultUuid_whenIdNull() {
        doReturn(responseWithOne(statusDocument(null, "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
        assertThat(result.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultUuid_whenIdIsNone() {
        Value none = mock(Value.class);
        when(none.isNone()).thenReturn(true);
        doReturn(responseWithOne(statusDocument(none, "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultModel_whenResponseNull() {
        when(surreal.query(anyString())).thenReturn(null);

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
        assertThat(result.getName()).isEmpty();
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultModel_whenSizeZero() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        when(surreal.query(anyString())).thenReturn(response);

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultModel_whenNotArray() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(false);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultModel_whenArrayEmpty() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(true);
        Array array = mock(Array.class);
        when(array.len()).thenReturn(0);
        when(statement.getArray()).thenReturn(array);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelById_returnsDefaultModel_whenFirstNotObject() {
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

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelById("st-1");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }

    @Test
    void findStatusTokenSurrealModelByName_returnsModel() {
        UUID id = UUID.randomUUID();
        doReturn(responseWithOne(statusDocument(uuidValue(id), "ACTIVE"))).when(surreal).query(anyString());

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelByName("ACTIVE");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void findStatusTokenSurrealModelByName_returnsDefaultModel_whenMissing() {
        when(surreal.query(anyString())).thenReturn(null);

        StatusTokenSurrealModel result = adapter.findStatusTokenSurrealModelByName("MISSING");

        assertThat(result.getId()).isEqualTo(DEFAULT_UUID);
    }
}
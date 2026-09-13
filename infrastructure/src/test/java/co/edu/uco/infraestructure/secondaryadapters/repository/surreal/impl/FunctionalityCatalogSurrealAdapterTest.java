package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunctionalityCatalogSurrealAdapterTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private FunctionalityCatalogSurrealAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(FunctionalityCatalogSurrealAdapter.class)).thenReturn(log);
        adapter = new FunctionalityCatalogSurrealAdapter(surreal, loggerFactory);
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

    private Object functionalityDocument(String uuid, String name, String appId) {
        Object doc = mock(Object.class);
        doReturn(recordIdValue("functionality", uuid)).when(doc).get("id");
        doReturn(stringValue(name)).when(doc).get("name");
        doReturn(recordIdValue("application", appId)).when(doc).get("application_id");
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

    @Test
    void findAllByApplicationId_mapsFunctionalitiesWithApplicationId() {
        String uuid = UUID.randomUUID().toString();
        String appId = UUID.randomUUID().toString();
        doReturn(responseWithOne(functionalityDocument(uuid, "Search", appId))).when(surreal).query(anyString());

        List<FunctionalityData> result = adapter.findAllByApplicationId(appId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Search");
        assertThat(result.get(0).getId()).isEqualTo(UUID.fromString(uuid));
        assertThat(result.get(0).getApplication().getId()).isEqualTo(UUID.fromString(appId));
    }

    @Test
    void findAllByApplicationId_returnsEmptyList_whenResponseEmpty() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findAllByApplicationId("app-1")).isEmpty();
    }
}
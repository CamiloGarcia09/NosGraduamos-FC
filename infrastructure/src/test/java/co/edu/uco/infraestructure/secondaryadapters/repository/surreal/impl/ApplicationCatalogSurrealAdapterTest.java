package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
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

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationCatalogSurrealAdapterTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;

    private ApplicationCatalogSurrealAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(ApplicationCatalogSurrealAdapter.class)).thenReturn(log);
        adapter = new ApplicationCatalogSurrealAdapter(surreal, loggerFactory);
    }

    @Test
    void findAll_mapsApplicationAndOrganizationId() {
        UUID applicationId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        doReturn(responseWith(applicationDocument(applicationId, organizationId))).when(surreal).query(anyString());

        List<ApplicationData> result = adapter.findAll();

        assertThat(result).hasSize(1);
        ApplicationData application = result.get(0);
        assertSoftly(softly -> {
            softly.assertThat(application.getId()).isEqualTo(applicationId);
            softly.assertThat(application.getName()).isEqualTo("Messages");
            softly.assertThat(application.getOrganization().getId()).isEqualTo(organizationId);
            softly.assertThat(application.getOrganization().getName()).isEmpty();
        });
    }

    @Test
    void findAll_returnsEmptyList_whenNoApplicationDocumentsExist() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        doReturn(response).when(surreal).query(anyString());

        assertThat(adapter.findAll()).isEmpty();
    }

    @Test
    void findAll_usesDefaultOrganizationId_whenStoredReferenceIsMalformed() {
        Object document = mock(Object.class);
        doReturn(recordIdValue("application", UUID.randomUUID())).when(document).get("id");
        doReturn(stringValue("Messages")).when(document).get("name");
        doReturn(rawStringValue("organization:not-a-uuid")).when(document).get("organization_id");
        doReturn(responseWith(document)).when(surreal).query(anyString());

        List<ApplicationData> result = adapter.findAll();

        assertThat(result).singleElement()
                .extracting(application -> application.getOrganization().getId())
                .isEqualTo(DEFAULT_UUID);
    }

    private Object applicationDocument(UUID applicationId, UUID organizationId) {
        Object document = mock(Object.class);
        doReturn(recordIdValue("application", applicationId)).when(document).get("id");
        doReturn(stringValue("Messages")).when(document).get("name");
        doReturn(recordIdValue("organization", organizationId)).when(document).get("organization_id");
        return document;
    }

    private Response responseWith(Object document) {
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

    private Value stringValue(String value) {
        Value result = mock(Value.class);
        when(result.isNull()).thenReturn(false);
        when(result.isNone()).thenReturn(false);
        when(result.isString()).thenReturn(true);
        when(result.getString()).thenReturn(value);
        return result;
    }

    private Value recordIdValue(String table, UUID id) {
        RecordId recordId = mock(RecordId.class);
        when(recordId.toString()).thenReturn(table + ":" + id);
        Value result = mock(Value.class);
        when(result.isRecordId()).thenReturn(true);
        when(result.getRecordId()).thenReturn(recordId);
        return result;
    }

    private Value rawStringValue(String value) {
        Value result = mock(Value.class);
        when(result.isRecordId()).thenReturn(false);
        when(result.isString()).thenReturn(true);
        when(result.getString()).thenReturn(value);
        return result;
    }
}

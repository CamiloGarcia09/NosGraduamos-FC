package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    private Object applicationDocument(String uuid, String name, String organizationId) {
        Object doc = mock(Object.class);
        doReturn(recordIdValue("application", uuid)).when(doc).get("id");
        doReturn(stringValue(name)).when(doc).get("name");
        doReturn(recordIdValue("organization", organizationId)).when(doc).get("organization_id");
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
        String organizationId = UUID.randomUUID().toString();
        doReturn(responseWithOne(applicationDocument(uuid, "App", organizationId))).when(surreal).query(anyString());

        Optional<ApplicationData> result = adapter.findByName("App");

        assertThat(result).hasValueSatisfying(application -> assertSoftly(softly -> {
            softly.assertThat(application.getName()).isEqualTo("App");
            softly.assertThat(application.getId()).isEqualTo(UUID.fromString(uuid));
            softly.assertThat(application.getOrganization().getId()).isEqualTo(UUID.fromString(organizationId));
        }));
    }

    @Test
    void findByName_returnsEmpty_whenResponseEmpty() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        assertThat(adapter.findByName("App")).isEmpty();
    }

    @Test
    void findById_returnsMappedApplicationAndUsesExactRecordQuery() {
        String applicationId = "123e4567-e89b-12d3-a456-426614174000";
        String organizationId = "223e4567-e89b-12d3-a456-426614174000";
        String query = "SELECT * FROM application:`" + applicationId + "` LIMIT 1;";
        doReturn(responseWithOne(applicationDocument(applicationId, "App", organizationId)))
                .when(surreal).query(query);

        Optional<ApplicationData> result = adapter.findById(applicationId);

        assertThat(result).hasValueSatisfying(application -> assertSoftly(softly -> {
            softly.assertThat(application.getId()).isEqualTo(UUID.fromString(applicationId));
            softly.assertThat(application.getName()).isEqualTo("App");
            softly.assertThat(application.getOrganization().getId()).isEqualTo(UUID.fromString(organizationId));
            softly.assertThat(application.getOrganization().getName()).isEmpty();
        }));
        verify(surreal).query(query);
    }

    @Test
    void findById_returnsEmpty_whenResponseEmpty() {
        doReturn(emptyResponse()).when(surreal).query(anyString());

        assertThat(adapter.findById("123e4567-e89b-12d3-a456-426614174000")).isEmpty();
    }

    @Test
    void findById_propagatesFailureAndLogsQuery_whenQueryFails() {
        String applicationId = "123e4567-e89b-12d3-a456-426614174000";
        String query = "SELECT * FROM application:`" + applicationId + "` LIMIT 1;";
        RuntimeException cause = new RuntimeException("db down");
        doThrow(cause).when(surreal).query(query);

        assertThatThrownBy(() -> adapter.findById(applicationId))
                .isSameAs(cause);
        verify(log).error(eq("Error al consultar aplicación por id en SurrealDB: " + query),
                any(RuntimeException.class));
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
    void create_persistsApplicationWithOrganizationUsingExactUpsert() {
        UUID applicationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID organizationId = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(organizationId);
        organization.setName("UCO");
        ApplicationData application = ApplicationData.build(applicationId, "App", organization);
        String expectedUpsert = "UPSERT application:`123e4567-e89b-12d3-a456-426614174000` CONTENT { "
                + "name: 'App', organization_id: organization:`223e4567-e89b-12d3-a456-426614174000`, "
                + "language_id: language_base:`lang-1`, state_id: application_state:`state-1` };";

        adapter.create(application, "lang-1", "state-1");

        verify(surreal).query(expectedUpsert);
        verify(log).info("Executing SurrealQL upsert application: {}", expectedUpsert);
        verifyNoMoreInteractions(surreal);
    }

    @Test
    void create_throwsBusinessException_whenQueryFails() {
        ApplicationData application = ApplicationData.build();
        doThrow(new RuntimeException("db down")).when(surreal).query(anyString());

        assertThatThrownBy(() -> adapter.create(application, "lang-1", "state-1"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getTechnicalMessage())
                        .isEqualTo("Error al persistir la aplicación en la base de datos SurrealDB"));
        verify(log).error(anyString(), any(RuntimeException.class));
    }
}

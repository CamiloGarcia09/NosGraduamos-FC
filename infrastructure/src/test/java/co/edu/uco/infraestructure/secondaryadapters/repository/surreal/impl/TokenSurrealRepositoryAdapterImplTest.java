package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenSurrealRepositoryAdapterImplTest {

    @Mock
    private Surreal surreal;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private CatalogPort catalogPort;

    private TokenSurrealRepositoryAdapterImpl adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(TokenSurrealRepositoryAdapterImpl.class)).thenReturn(log);
        lenient().when(catalogPort.getMessage(org.mockito.ArgumentMatchers.anyString())).thenReturn("msg");
        CatalogPortStaticRef.set(catalogPort);
        adapter = new TokenSurrealRepositoryAdapterImpl(surreal, loggerFactory);
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

    private Value recordIdValue(String id) {
        Id idPart = mock(Id.class);
        when(idPart.toString()).thenReturn(id);
        RecordId recordId = mock(RecordId.class);
        when(recordId.getId()).thenReturn(idPart);
        Value v = mock(Value.class);
        when(v.isRecordId()).thenReturn(true);
        when(v.getRecordId()).thenReturn(recordId);
        return v;
    }

    private Value dateTimeValue(LocalDateTime dateTime) {
        Value v = mock(Value.class);
        when(v.isNull()).thenReturn(false);
        when(v.isNone()).thenReturn(false);
        when(v.isDateTime()).thenReturn(true);
        when(v.getDateTime()).thenReturn(ZonedDateTime.of(dateTime, ZoneOffset.UTC));
        return v;
    }

    private Object tokenDocument() {
        Object doc = mock(Object.class);
        doReturn(recordIdValue("tok-1")).when(doc).get("id");
        doReturn(stringValue("secret")).when(doc).get("secret_name");
        doReturn(dateTimeValue(LocalDateTime.of(2025, 1, 1, 10, 0))).when(doc).get("creation_date");
        doReturn(dateTimeValue(LocalDateTime.of(2026, 1, 1, 10, 0))).when(doc).get("expiration_date");
        doReturn(stringValue("env-1")).when(doc).get("environment_id");
        doReturn(stringValue("st-1")).when(doc).get("state_id");
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
    void upsert_executesQueryAndReturnsModel() {
        TokenSurrealModel model = new TokenSurrealModel("tok-1", "secret",
                LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), "env-1", "st-1");
        doReturn(mock(Response.class)).when(surreal).query(anyString());

        TokenSurrealModel result = adapter.upsert(model);

        assertThat(result).isSameAs(model);
        verify(surreal).query(anyString());
    }

    @Test
    void upsert_rethrowsRuntimeException() {
        TokenSurrealModel model = new TokenSurrealModel("tok-1", "secret",
                LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), "env-1", "st-1");
        when(surreal.query(anyString())).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> adapter.upsert(model))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");
        verify(log).error(anyString(), any(RuntimeException.class));
    }

    @Test
    void findTokenSurrealModelById_returnsModel_whenFound() {
        doReturn(responseWithOne(tokenDocument())).when(surreal).query(anyString());

        Optional<TokenSurrealModel> result = adapter.findTokenSurrealModelById("tok-1");

        assertThat(result).isPresent();
        TokenSurrealModel model = result.get();
        assertThat(model.getId()).isEqualTo("tok-1");
        assertThat(model.getSecretName()).isEqualTo("secret");
        assertThat(model.getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertThat(model.getExpirationDate()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertThat(model.getEnvironmentId()).isEqualTo("env-1");
        assertThat(model.getStateId()).isEqualTo("st-1");
    }

    @Test
    void findTokenSurrealModelById_returnsEmpty_whenResponseNull() {
        when(surreal.query(anyString())).thenReturn(null);

        assertThat(adapter.findTokenSurrealModelById("tok-1")).isEmpty();
    }

    @Test
    void findTokenSurrealModelById_returnsEmpty_whenSizeZero() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(0);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findTokenSurrealModelById("tok-1")).isEmpty();
    }

    @Test
    void findTokenSurrealModelById_returnsEmpty_whenNotArray() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(false);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findTokenSurrealModelById("tok-1")).isEmpty();
    }

    @Test
    void findTokenSurrealModelById_returnsEmpty_whenArrayEmpty() {
        Response response = mock(Response.class);
        when(response.size()).thenReturn(1);
        Value statement = mock(Value.class);
        when(statement.isArray()).thenReturn(true);
        Array array = mock(Array.class);
        when(array.len()).thenReturn(0);
        when(statement.getArray()).thenReturn(array);
        when(response.take(0)).thenReturn(statement);
        when(surreal.query(anyString())).thenReturn(response);

        assertThat(adapter.findTokenSurrealModelById("tok-1")).isEmpty();
    }

    @Test
    void findTokenSurrealModelById_returnsEmpty_whenFirstNotObject() {
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

        assertThat(adapter.findTokenSurrealModelById("tok-1")).isEmpty();
    }

    @Test
    void findTokenSurrealModelById_handlesStringIdAndMissingDates() {
        Object doc = mock(Object.class);
        Value idValue = mock(Value.class);
        when(idValue.isString()).thenReturn(true);
        when(idValue.getString()).thenReturn("token:tok-2");
        doReturn(idValue).when(doc).get("id");
        doReturn(stringValue("secret")).when(doc).get("secret_name");
        when(doc.get("creation_date")).thenReturn(null);
        when(doc.get("expiration_date")).thenReturn(null);
        doReturn(stringValue("env-2")).when(doc).get("environment_id");
        when(doc.get("state_id")).thenReturn(null);
        doReturn(responseWithOne(doc)).when(surreal).query(anyString());

        Optional<TokenSurrealModel> result = adapter.findTokenSurrealModelById("tok-2");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("token:tok-2");
        assertThat(result.get().getCreationDate()).isNotNull();
        assertThat(result.get().getStateId()).isEmpty();
    }

    @Test
    void findTokenSurrealModelByEnvironmentIdAndStateId_returnsModel() {
        doReturn(responseWithOne(tokenDocument())).when(surreal).query(anyString());

        Optional<TokenSurrealModel> result = adapter.findTokenSurrealModelByEnvironmentIdAndStateId("env-1", "st-1");

        assertThat(result).isPresent();
        assertThat(result.get().getEnvironmentId()).isEqualTo("env-1");
        assertThat(result.get().getStateId()).isEqualTo("st-1");
    }

    @Test
    void findTokenSurrealModelByEnvironmentIdAndStateId_returnsEmpty() {
        when(surreal.query(anyString())).thenReturn(null);

        assertThat(adapter.findTokenSurrealModelByEnvironmentIdAndStateId("env-1", "st-1")).isEmpty();
    }
}
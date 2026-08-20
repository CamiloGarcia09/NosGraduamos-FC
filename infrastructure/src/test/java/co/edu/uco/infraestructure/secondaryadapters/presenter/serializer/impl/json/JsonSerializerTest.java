package co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.impl.json;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.Response;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonSerializerTest {

    private final JsonSerializer serializer = new JsonSerializer();

    @AfterEach
    void tearDown() {
        CatalogPortStaticRef.set(null);
    }

    @Test
    void getSupportedContentType_returnsApplicationJson() {
        assertThat(serializer.getSupportedContentType()).isEqualTo("application/json");
    }

    @Test
    void isDefault_returnsTrue() {
        assertThat(serializer.isDefault()).isTrue();
    }

    @Test
    void serialize_serializesRecordToJson() throws Exception {
        Response<String> response = new Response<>(List.of("hola"), List.of());

        String json = serializer.serialize(response);

        assertThat(json).contains("\"hola\"");
        assertThat(json).startsWith("{");
        assertThat(json).endsWith("}");
    }

    @Test
    void serialize_serializesLocalDateTimeWithJavaTimeModule() throws Exception {
        record Dated(java.time.LocalDateTime time, String name) {
        }
        Dated data = new Dated(java.time.LocalDateTime.of(2025, 1, 1, 10, 30), "x");

        String json = serializer.serialize(data);

        assertThat(json).contains("[2025,1,1,10,30]");
    }

    @Test
    void serialize_throwsCrossWordsException_onSerializationError() {
        CatalogPort catalogPort = mock(CatalogPort.class);
        when(catalogPort.getMessage("TCH_018")).thenReturn("serialization error");
        CatalogPortStaticRef.set(catalogPort);

        Object cyclic = new Object() {
            @SuppressWarnings("unused")
            public Object self = this;
        };

        assertThatThrownBy(() -> serializer.serialize(cyclic))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("serialization error"));
    }
}
package co.edu.uco.infraestructure.secondaryadapters.parameter.dummy;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogParameterAdapterTest {

    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private CatalogPort catalogPort;

    private CatalogParameterAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(CatalogParameterAdapter.class)).thenReturn(log);
        adapter = new CatalogParameterAdapter(loggerFactory, catalogPort);
    }

    @Test
    void consultarParametro_throwsCrossWordsException_whenCodeIsEmpty() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_068.getCode())).thenReturn("El codigo es requerido");

        assertThatThrownBy(() -> adapter.consultarParametro(""))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getUserMessage()).isEqualTo("El codigo es requerido"));
    }

    @Test
    void consultarParametro_throwsCrossWordsException_whenCodeIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_068.getCode())).thenReturn("El codigo es requerido");

        assertThatThrownBy(() -> adapter.consultarParametro(null))
                .isInstanceOf(CrossWordsException.class);
    }

    @Test
    void consultarParametro_returnsValue_whenKeyExists() {
        assertThat(adapter.consultarParametro("SERVER-PORT")).isEqualTo("server.port");
    }

    @Test
    void consultarParametro_throwsCrossWordsException_whenKeyNotFound() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_068.getCode())).thenReturn("No existe el parametro %s");

        assertThatThrownBy(() -> adapter.consultarParametro("NON-EXISTENT"))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("No existe el parametro NON-EXISTENT"));
        verify(log).error(anyString(), any(RuntimeException.class));
    }
}
package co.edu.uco.infraestructure.secondaryadapters.parameter.dummy;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
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
import static org.mockito.ArgumentMatchers.eq;
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
    void consultarParametro_returnsValueFromProperties() {
        assertThat(adapter.consultarParametro("SERVER-PORT")).isEqualTo("server.port");
    }

    @Test
    void consultarParametro_trimsCode() {
        assertThat(adapter.consultarParametro("  SERVER-NAME  ")).isEqualTo("spring.application.name");
    }

    @Test
    void consultarParametro_throwsBusinessException_whenCodeIsEmpty() {
        when(catalogPort.getMessage("TCH_055")).thenReturn("empty code");

        assertThatThrownBy(() -> adapter.consultarParametro(" "))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("empty code"));
    }

    @Test
    void consultarParametro_throwsCrossWordsException_whenKeyNotFound() {
        when(catalogPort.getMessage("TCH_057")).thenReturn("Parameter %s not found");

        assertThatThrownBy(() -> adapter.consultarParametro("UNKNOWN-KEY"))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("Parameter UNKNOWN-KEY not found"));
        verify(log).error(eq("Parameter UNKNOWN-KEY not found"), any(Throwable.class));
    }
}
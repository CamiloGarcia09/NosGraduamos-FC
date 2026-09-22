package co.edu.uco.infraestructure.secondaryadapters.security;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAdapterTest {

    @Mock
    private LoggingPortFactory loggingPortFactory;
    @Mock
    private LoggingPort log;

    private SecurityAdapter adapter;

    @BeforeEach
    void setUp() {
        when(loggingPortFactory.getLogger(SecurityAdapter.class)).thenReturn(log);
        adapter = new SecurityAdapter(loggingPortFactory);
    }

    @Test
    void validateAccessToken_doesNotLogToken_whenTokenIsInvalid() {
        assertThat(adapter.validateAccessToken("sensitive-token")).isFalse();

        verify(log).info("Validando token quemado -> false");
    }

    @Test
    void validateAccessToken_doesNotLogToken_whenTokenIsValid() {
        String token = adapter.generateAccessToken("application-id", "environment-id", LocalDateTime.MAX);

        assertThat(adapter.validateAccessToken(token)).isTrue();
        verify(log).info("Validando token quemado -> true");
    }
}

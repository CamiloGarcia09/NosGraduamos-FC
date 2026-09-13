package co.edu.uco.application.primaryports.facade.token.impl;

import co.edu.uco.application.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindEnvironmentIdTokenUseCaseFacadeImplTest {

    @Mock
    private HandlingFindEnvironmentIdTokenPort handlingPort;

    @InjectMocks
    private FindEnvironmentIdTokenUseCaseFacadeImpl facade;

    @Test
    void execute_delegatesToHandlingPortAndReturnsResult() {
        when(handlingPort.execute("token-123")).thenReturn("env-456");

        String result = facade.execute("token-123");

        assertThat(result).isEqualTo("env-456");
        verify(handlingPort).execute("token-123");
    }
}

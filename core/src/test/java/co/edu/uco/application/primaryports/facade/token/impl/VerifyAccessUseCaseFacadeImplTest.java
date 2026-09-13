package co.edu.uco.application.primaryports.facade.token.impl;

import co.edu.uco.application.usecase.handling.HandlingVerifyAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyAccessUseCaseFacadeImplTest {

    @Mock
    private HandlingVerifyAccessPort handlingPort;

    @InjectMocks
    private VerifyAccessUseCaseFacadeImpl facade;

    @Test
    void execute_delegatesToHandlingPortAndReturnsTrue() {
        when(handlingPort.verifyAccess("valid-token")).thenReturn(true);

        boolean result = facade.execute("valid-token");

        assertThat(result).isTrue();
        verify(handlingPort).verifyAccess("valid-token");
    }

    @Test
    void execute_delegatesToHandlingPortAndReturnsFalse() {
        when(handlingPort.verifyAccess("expired-token")).thenReturn(false);

        boolean result = facade.execute("expired-token");

        assertThat(result).isFalse();
        verify(handlingPort).verifyAccess("expired-token");
    }
}

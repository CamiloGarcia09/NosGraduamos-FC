package co.edu.uco.application.primaryports.facade.context.impl;

import co.edu.uco.application.primaryports.dto.context.ActiveContextDTO;
import co.edu.uco.application.primaryports.dto.context.AvailableContextDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActiveContextUseCaseFacadeImplTest {

    private final HandlingActiveContextPort port = mock(HandlingActiveContextPort.class);
    private final ActiveContextUseCaseFacadeImpl facade = new ActiveContextUseCaseFacadeImpl(port);
    private final ExternalIdentity identity =
            new ExternalIdentity("issuer", "subject", null, Instant.MAX);

    @Test
    void methods_delegateAndReturnPortResults() {
        SelectActiveContextDTO selection = new SelectActiveContextDTO("org", "app", "env");
        ActiveContextDTO active = ActiveContextDTO.builder().environmentId("env").build();
        List<AvailableContextDTO> available = List.of(AvailableContextDTO.builder().build());
        when(port.findAvailableContexts(identity)).thenReturn(available);
        when(port.findActiveContext(identity)).thenReturn(active);
        when(port.selectActiveContext(selection, identity)).thenReturn(active);

        assertThat(facade.findAvailableContexts(identity)).isSameAs(available);
        assertThat(facade.findActiveContext(identity)).isSameAs(active);
        assertThat(facade.selectActiveContext(selection, identity)).isSameAs(active);
        verify(port).findAvailableContexts(identity);
        verify(port).findActiveContext(identity);
        verify(port).selectActiveContext(selection, identity);
    }
}

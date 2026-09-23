package co.edu.uco.application.primaryports.facade.environment.impl;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.handling.HandlingCreateEnvironmentPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateEnvironmentUseCaseFacadeImplTest {

    @Mock
    private HandlingCreateEnvironmentPort handlingCreateEnvironmentPort;

    private CreateEnvironmentUseCaseFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new CreateEnvironmentUseCaseFacadeImpl(handlingCreateEnvironmentPort);
    }

    @Test
    void execute_delegatesEnvironmentAndIdentity() {
        CreateEnvironmentDTO dto = new CreateEnvironmentDTO();
        ExternalIdentity identity = new ExternalIdentity(
                "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

        facade.execute(dto, identity);

        verify(handlingCreateEnvironmentPort).createEnvironment(dto, identity);
    }

    @Test
    void execute_delegatesLegacyNullIdentity() {
        CreateEnvironmentDTO dto = new CreateEnvironmentDTO();

        facade.execute(dto, null);

        verify(handlingCreateEnvironmentPort).createEnvironment(dto, null);
    }
}

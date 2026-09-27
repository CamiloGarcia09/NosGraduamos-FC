package co.edu.uco.application.primaryports.facade.functionality.impl;

import co.edu.uco.application.primaryports.dto.functionality.CreateFunctionalityDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import co.edu.uco.application.usecase.handling.HandlingCreateFunctionalityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateFunctionalityUseCaseFacadeImplTest {

    @Mock
    private HandlingCreateFunctionalityPort handlingCreateFunctionalityPort;

    private CreateFunctionalityUseCaseFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new CreateFunctionalityUseCaseFacadeImpl(handlingCreateFunctionalityPort);
    }

    @Test
    void execute_delegatesFunctionalityAndIdentity() {
        CreateFunctionalityDTO dto = new CreateFunctionalityDTO();
        ExternalIdentity identity = new ExternalIdentity(
                "issuer", "subject", "user@example.com", PrincipalType.HUMAN, Instant.MAX);

        facade.execute(dto, identity);

        verify(handlingCreateFunctionalityPort).createFunctionality(dto, identity);
    }

    @Test
    void execute_delegatesLegacyNullIdentity() {
        CreateFunctionalityDTO dto = new CreateFunctionalityDTO();

        facade.execute(dto, null);

        verify(handlingCreateFunctionalityPort).createFunctionality(dto, null);
    }
}

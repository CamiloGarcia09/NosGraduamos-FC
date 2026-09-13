package co.edu.uco.application.primaryports.facade.environment.impl;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.usecase.handling.HandlingCreateEnvironmentPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void execute_delegatesToHandlingPort() {
        CreateEnvironmentDTO dto = new CreateEnvironmentDTO();

        facade.execute(dto);

        verify(handlingCreateEnvironmentPort).createEnvironment(dto);
    }
}
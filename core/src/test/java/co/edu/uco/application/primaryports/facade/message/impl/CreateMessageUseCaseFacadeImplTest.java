package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.usecase.handling.HandlingCreateMessagePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateMessageUseCaseFacadeImplTest {

    @Mock
    private HandlingCreateMessagePort handlingCreateMessagePort;

    @InjectMocks
    private CreateMessageUseCaseFacadeImpl facade;

    @Test
    void execute_delegatesMessageAndAuthenticatedEnvironment() {
        CreateMessageDTO dto = new CreateMessageDTO();

        facade.execute(dto, "env-1");

        verify(handlingCreateMessagePort).createMessage(dto, "env-1");
    }
}

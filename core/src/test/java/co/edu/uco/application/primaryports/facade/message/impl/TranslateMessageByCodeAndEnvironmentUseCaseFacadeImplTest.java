package co.edu.uco.application.primaryports.facade.message.impl;

import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.usecase.handling.HandlingTranslateMessageByCodeAndEnvironmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslateMessageByCodeAndEnvironmentUseCaseFacadeImplTest {

    @Mock
    private HandlingTranslateMessageByCodeAndEnvironmentPort handlingPort;

    @InjectMocks
    private TranslateMessageByCodeAndEnvironmentUseCaseFacadeImpl facade;

    @Test
    void execute_delegatesToHandlingPortAndReturnsResult() {
        TranslatedMessageDTO expected = TranslatedMessageDTO.create(
                "CODE001", "en", "es", "title", "content",
                "título", "contenido", "type", "category", "app", "func",
                "provider", "model", 100L
        );
        when(handlingPort.execute("CODE001", "env-123", "en", "es")).thenReturn(expected);

        TranslatedMessageDTO result = facade.execute("CODE001", "env-123", "en", "es");

        assertThat(result).isSameAs(expected);
        verify(handlingPort).execute("CODE001", "env-123", "en", "es");
    }
}

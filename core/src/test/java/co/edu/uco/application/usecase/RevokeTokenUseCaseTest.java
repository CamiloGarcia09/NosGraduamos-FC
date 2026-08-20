package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevokeTokenUseCaseTest {

    @Mock
    private FindTokenRepository findTokenRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenStateRepository tokenStateRepository;

    @InjectMocks
    private RevokeTokenUseCase useCase;

    private final UUID inactiveStateId = UUID.randomUUID();

    @Test
    void execute_revokesToken_whenActiveTokenFound() {
        StatusTokenData inactiveState = new StatusTokenData(inactiveStateId, "Inactive");
        when(tokenStateRepository.findByStatusName("Inactive")).thenReturn(inactiveState);

        TokenData token = new TokenData();
        token.setEnvironmentId("env");
        when(findTokenRepository.findByEnvironmentAndState("env", "123e4567-e89b-12d3-a456-426614175000"))
                .thenReturn(Optional.of(token));

        useCase.execute("env", "123e4567-e89b-12d3-a456-426614175000");

        assertThat(token.getStateId()).isEqualTo(inactiveStateId.toString());
        verify(tokenRepository).save(token);
    }

    @Test
    void execute_doesNotSave_whenNoActiveTokenFound() {
        StatusTokenData inactiveState = new StatusTokenData(inactiveStateId, "Inactive");
        when(tokenStateRepository.findByStatusName("Inactive")).thenReturn(inactiveState);
        when(findTokenRepository.findByEnvironmentAndState("env", "123e4567-e89b-12d3-a456-426614175000"))
                .thenReturn(Optional.empty());

        useCase.execute("env", "123e4567-e89b-12d3-a456-426614175000");

        verifyNoInteractions(tokenRepository);
    }
}
package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenStateRepository;
import co.edu.uco.core.domain.usecase.handling.HandlingRevokeTokenPort;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.STATE_INACTIVE;
import static co.edu.uco.utils.helper.UtilUUID.getStringFromUUID;

@Component
public final class RevokeTokenUseCase implements HandlingRevokeTokenPort {
    private final FindTokenRepository findTokenRepository;
    private final TokenRepository tokenRepository;
    private final TokenStateRepository tokenStateRepository;
    public RevokeTokenUseCase(FindTokenRepository findTokenRepository, TokenRepository tokenRepository, TokenStateRepository tokenStateRepository) {
        this.findTokenRepository = findTokenRepository;
        this.tokenRepository = tokenRepository;
        this.tokenStateRepository = tokenStateRepository;
    }
    @Override
    public void execute(String environment, String state) {
        var stateId = tokenStateRepository.findByStatusName(STATE_INACTIVE).getId();
        findTokenRepository.findByEnvironmentAndState(environment, state)
                .ifPresent(token -> {
                    token.setStateId(getStringFromUUID(stateId));
                    tokenRepository.save(token);
                });
    }
}
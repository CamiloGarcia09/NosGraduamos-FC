package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenCachePort;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.port.out.repository.token.TokenStateRepository;
import co.edu.uco.core.domain.port.out.secret.EncryptTokenPort;
import co.edu.uco.core.domain.usecase.handling.HandlingVerifyAccessPort;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.Map;

import static co.edu.uco.core.CrosswordsConstant.*;

@Service
public final class VerifyAccessUseCase implements HandlingVerifyAccessPort {
    private final EncryptTokenPort encryptTokenPort;
    private final FindTokenCachePort findTokenCachePort;
    private final FindTokenRepository findTokenRepository;
    private final TokenStateRepository tokenStateRepository;
    private final InMemoryCatalog inMemoryCatalog;
    public VerifyAccessUseCase(EncryptTokenPort encryptTokenPort, FindTokenCachePort findTokenCachePort, FindTokenRepository findTokenRepository, TokenStateRepository tokenStateRepository, InMemoryCatalog inMemoryCatalog) {
        this.encryptTokenPort = encryptTokenPort;
        this.findTokenCachePort = findTokenCachePort;
        this.findTokenRepository = findTokenRepository;
        this.tokenStateRepository = tokenStateRepository;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public boolean verifyAccess(String token) {
        var secretName = findTokenRepository.findById(token);
        stateValid(secretName.getStateId());
        Map<String, String> secret = findTokenCachePort.getSecret(secretName.getSecretName());
        boolean result;
        try{
            result = encryptTokenPort.access(
                    secret.get(SECRET_PORT_PRIVATE_KEY),
                    token,
                    secret.get(SECRET_PORT_SECRET_NAME)
            );
        } catch (Exception e){
            return false;
        }
        return result;
    }
    private void stateValid(String statusId) {
        var status = tokenStateRepository.findByStatus(statusId);
        if (!status.getName().equals(STATE_ACTIVE)) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.TCH_033.getKey()));
        }
    }
}
package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenCachePort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.application.usecase.handling.HandlingVerifyAccessPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.Map;

import static co.edu.uco.application.CrosswordsConstant.*;

@Service
public final class VerifyAccessUseCase implements HandlingVerifyAccessPort {

    private final EncryptTokenPort encryptTokenPort;
    private final FindTokenCachePort findTokenCachePort;
    private final FindTokenRepository findTokenRepository;
    private final TokenStateRepository tokenStateRepository;
    private final CatalogPort catalogPort;

    public VerifyAccessUseCase(EncryptTokenPort encryptTokenPort, FindTokenCachePort findTokenCachePort,
                               FindTokenRepository findTokenRepository, TokenStateRepository tokenStateRepository,
                               CatalogPort catalogPort) {
        this.encryptTokenPort = encryptTokenPort;
        this.findTokenCachePort = findTokenCachePort;
        this.findTokenRepository = findTokenRepository;
        this.tokenStateRepository = tokenStateRepository;
        this.catalogPort = catalogPort;
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
            throw BusinessRuleException.buildUserException(catalogPort.getMessage("TCH_033"));
        }
    }
}
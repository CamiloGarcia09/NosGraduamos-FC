package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenCachePort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenStateRepository;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.application.usecase.handling.HandlingVerifyAccessPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

import static co.edu.uco.application.CrosswordsConstant.*;
import static co.edu.uco.crosscutting.helpers.UtilDate.nowUtc;

@Service
public final class VerifyAccessUseCase implements HandlingVerifyAccessPort {

    private final EncryptTokenPort encryptTokenPort;
    private final FindTokenCachePort findTokenCachePort;
    private final FindTokenRepository findTokenRepository;
    private final TokenStateRepository tokenStateRepository;
    private final CatalogPort catalogPort;
    private final Clock clock;

    public VerifyAccessUseCase(EncryptTokenPort encryptTokenPort, FindTokenCachePort findTokenCachePort,
                               FindTokenRepository findTokenRepository, TokenStateRepository tokenStateRepository,
                               CatalogPort catalogPort, Clock clock) {
        this.encryptTokenPort = encryptTokenPort;
        this.findTokenCachePort = findTokenCachePort;
        this.findTokenRepository = findTokenRepository;
        this.tokenStateRepository = tokenStateRepository;
        this.catalogPort = catalogPort;
        this.clock = clock;
    }

    @Override
    public boolean verifyAccess(String token) {
        TokenData tokenData = findToken(token);
        stateValid(tokenData.getStateId());
        expirationDateValid(tokenData.getExpirationDate());
        Map<String, String> secret = findTokenCachePort.getSecret(tokenData.getSecretName());
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

    private TokenData findToken(String token) {
        try {
            return findTokenRepository.findById(token);
        } catch (NotFoundException exception) {
            throw unauthorized(MessageCatalogCodeEnum.TCH_031);
        }
    }

    private void stateValid(String statusId) {
        var status = tokenStateRepository.findByStatus(statusId);
        if (!status.getName().equals(STATE_ACTIVE)) {
            throw unauthorized(MessageCatalogCodeEnum.TCH_033);
        }
    }

    private void expirationDateValid(LocalDateTime expirationDate) {
        if (!expirationDate.isAfter(nowUtc(clock))) {
            throw unauthorized(MessageCatalogCodeEnum.TCH_033);
        }
    }

    private UnauthorizedException unauthorized(MessageCatalogCodeEnum code) {
        return UnauthorizedException.buildUserException(catalogPort.getMessage(code.getCode()));
    }
}

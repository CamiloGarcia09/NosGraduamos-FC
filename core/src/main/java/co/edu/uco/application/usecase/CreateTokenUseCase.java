package co.edu.uco.application.usecase;

import co.edu.uco.application.common.mapper.entity.impl.TokenEntityMapper;
import co.edu.uco.application.usecase.domain.TokenDomain;
import co.edu.uco.application.secondaryports.repository.token.TokenRepository;
import co.edu.uco.application.usecase.handling.HandlingCreateTokenPort;
import org.springframework.stereotype.Component;

import static co.edu.uco.application.CrosswordsConstant.TOKEN_STATE_ACTIVE_ID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getStringToUUID;

@Component
public final class CreateTokenUseCase implements HandlingCreateTokenPort {
    private final TokenRepository tokenRepository;
    private final TokenEntityMapper tokenEntityMapper;
    public CreateTokenUseCase(TokenRepository tokenRepository, TokenEntityMapper tokenEntityMapper) {
        this.tokenRepository = tokenRepository;
        this.tokenEntityMapper = tokenEntityMapper;
    }
    @Override
    public void createToken(TokenDomain tokenDomain) {
        tokenDomain.setStateId(getStringToUUID(TOKEN_STATE_ACTIVE_ID));
        tokenEntityMapper.mapperDomain(tokenRepository.save(tokenEntityMapper.mapperData(tokenDomain)));
    }
}
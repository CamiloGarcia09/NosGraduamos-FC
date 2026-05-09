package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.mapper.entity.impl.TokenEntityMapper;
import co.edu.uco.core.domain.domains.TokenDomain;
import co.edu.uco.core.domain.port.out.repository.token.TokenRepository;
import co.edu.uco.core.domain.usecase.handling.HandlingCreateTokenPort;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.TOKEN_STATE_ACTIVE_ID;
import static co.edu.uco.utils.helper.UtilUUID.getStringToUUID;

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
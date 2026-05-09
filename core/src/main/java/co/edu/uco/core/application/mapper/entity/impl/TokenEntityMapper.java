package co.edu.uco.core.application.mapper.entity.impl;

import co.edu.uco.core.application.dto.token.TokenDTO;
import co.edu.uco.core.application.mapper.entity.DataMapper;
import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.core.domain.domains.TokenDomain;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class TokenEntityMapper implements DataMapper<TokenData, TokenDomain,TokenDTO> {
    private final ModelMapper mapper;

    public TokenEntityMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public TokenDomain mapperDomain(TokenData entity) {
        return mapper.map(entity, TokenDomain.class);
    }

    public TokenData mapperData(TokenDomain domain) {
        return mapper.map(domain, TokenData.class);
    }

    @Override
    public TokenDTO mapperDTO(TokenData entity) {
        return null;
    }

}

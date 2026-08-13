package co.edu.uco.application.common.mapper.entity.impl;

import co.edu.uco.application.primaryports.dto.token.TokenDTO;
import co.edu.uco.application.common.mapper.entity.DataMapper;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.usecase.domain.TokenDomain;
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

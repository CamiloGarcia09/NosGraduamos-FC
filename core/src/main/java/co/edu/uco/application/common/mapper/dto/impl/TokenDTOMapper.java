package co.edu.uco.application.common.mapper.dto.impl;

import co.edu.uco.application.primaryports.dto.token.TokenDTO;
import co.edu.uco.application.common.mapper.dto.DTOMapper;
import co.edu.uco.application.usecase.domain.TokenDomain;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class TokenDTOMapper implements DTOMapper<TokenDTO, TokenDomain> {
    private final ModelMapper modelMapper;
    public TokenDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public TokenDomain mapperDomain(TokenDTO dto) {
        return modelMapper.map(dto, TokenDomain.class);
    }
    @Override
    public TokenDTO mapperDTO(TokenDomain domain) {
        return modelMapper.map(domain, TokenDTO.class);
    }
}
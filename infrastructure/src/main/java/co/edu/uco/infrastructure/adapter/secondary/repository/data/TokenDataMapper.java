package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.infrastructure.adapter.secondary.repository.entity.TokenEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilUUID.getStringToUUID;

@Component
public final class TokenDataMapper implements DataMapper<TokenData, TokenEntity> {
    private final ModelMapper mapper;
    public TokenDataMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public TokenData mapperData(TokenEntity model) {
        return mapper.map(model, TokenData.class);
    }
    @Override
    public TokenEntity mapperModel(TokenData data) {
        return new TokenEntity(data.getId(), data.getCreationDate(), data.getSecretName(),
                data.getExpirationDate(), getStringToUUID(data.getEnvironmentId()),
                getStringToUUID(data.getStateId()));
    }
}
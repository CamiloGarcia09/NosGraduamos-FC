package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.TokenData;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.TokenDocument;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class TokenDocumentMapper implements DataMapper<TokenData, TokenDocument> {
    private final ModelMapper modelMapper;
    public TokenDocumentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public TokenData mapperData(TokenDocument model) {
        return modelMapper.map(model, TokenData.class);
    }
    @Override
    public TokenDocument mapperModel(TokenData data) {
        return modelMapper.map(data, TokenDocument.class);
    }
}
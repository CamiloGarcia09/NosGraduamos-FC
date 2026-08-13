package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.impl.json;

import co.edu.uco.core.application.catalog.CatalogPortStaticRef;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.AbstractSerializer;
import co.edu.uco.utils.exception.CrossWordsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.JSON_SERIALIZER_CONTENT_TYPE;

public final class JsonSerializer extends AbstractSerializer {
    public JsonSerializer() {
        super(JSON_SERIALIZER_CONTENT_TYPE);
    }
    @Override
    public <T> String serialize(T data) throws CrossWordsException {
        try{
            var mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw CrossWordsException.build(CatalogPortStaticRef.getMessage("TCH_018"), e);
        }
    }
    @Override
    public boolean isDefault() {
        return true;
    }
}
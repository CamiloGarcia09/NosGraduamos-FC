package co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.impl.json;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.AbstractSerializer;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.JSON_SERIALIZER_CONTENT_TYPE;

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
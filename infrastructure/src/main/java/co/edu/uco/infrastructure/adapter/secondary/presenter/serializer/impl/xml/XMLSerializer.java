package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.impl.xml;

import co.edu.uco.core.application.catalog.InMemoryCatalogStaticRef;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.AbstractSerializer;
import co.edu.uco.utils.exception.CrossWordsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.XML_SERIALIZER_CONTENT_TYPE;

public final class XMLSerializer extends AbstractSerializer {
    public XMLSerializer() {
        super(XML_SERIALIZER_CONTENT_TYPE);
    }
    @Override
    public <T> String serialize(T data) {
        try {
            var xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JavaTimeModule());
            return xmlMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw CrossWordsException.build(InMemoryCatalogStaticRef.getContent(MessageKeyEnum.TCH_018.getKey()), e);
        }
    }
}
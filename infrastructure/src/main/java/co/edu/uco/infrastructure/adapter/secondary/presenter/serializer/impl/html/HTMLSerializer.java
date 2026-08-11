package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.impl.html;

import co.edu.uco.core.application.catalog.InMemoryCatalogStaticRef;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.AbstractSerializer;
import co.edu.uco.utils.exception.CrossWordsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;

public final class HTMLSerializer extends AbstractSerializer {
    public HTMLSerializer() {
        super(HTML_SERIALIZER_CONTENT_TYPE);
    }
    @Override
    public <T> String serialize(T data) {
        try {
            var html = new StringBuilder();
            var objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            var result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            html.append(HTML_OPEN_TAG).append(BODY_OPEN_TAG);
            html.append(PRE_OPEN_TAG).append(result).append(PRE_CLOSE_TAG);
            html.append(BODY_CLOSE_TAG).append(HTML_CLOSE_TAG);
            return html.toString();
        } catch (Exception e) {
            throw CrossWordsException.build(InMemoryCatalogStaticRef.getContent(MessageKeyEnum.TCH_018.getKey()), e);
        }
    }
}
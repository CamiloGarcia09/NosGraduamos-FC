package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.impl.text;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.AbstractSerializer;
import co.edu.uco.utils.exception.CrossWordsException;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.TEXT_SERIALIZER_CONTENT_TYPE;

public final class PlainTextSerializer extends AbstractSerializer {
    public PlainTextSerializer() {
        super(TEXT_SERIALIZER_CONTENT_TYPE);
    }
    @Override
    public <T> String serialize(T data) throws CrossWordsException {
        try {
            return data.toString();
        } catch (Exception e) {
            throw CrossWordsException.build(DetailMessageEnum.TCH_018.getContent(), e);
        }
    }
}
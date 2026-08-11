package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public final class SerializerRegistry {
    private final List<SerializerType> serializers;
    private final InMemoryCatalog inMemoryCatalog;
    public SerializerRegistry(List<SerializerType> serializers, InMemoryCatalog inMemoryCatalog) {
        this.serializers = serializers;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    public SerializerType getSerializerForMediaType(String mediaType) {
        Optional<SerializerType> serializer = serializers.stream()
                .filter(s -> s.supports(mediaType))
                .findFirst();
        return serializer.orElseGet(() -> {
            Optional<SerializerType> defaultSerializer = serializers.stream()
                    .filter(SerializerType::isDefault)
                    .findFirst();
            if (defaultSerializer.isPresent()) {
                return defaultSerializer.get();
            } else {
                log.error(inMemoryCatalog.getContent(MessageKeyEnum.TCH_017.getKey()), mediaType);
                return null;
            }
        });
    }
}
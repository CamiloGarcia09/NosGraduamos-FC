package co.edu.uco.infraestructure.secondaryadapters.presenter.serializer;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public final class SerializerRegistry {
    private final List<SerializerType> serializers;
    private final CatalogPort catalogPort;
    public SerializerRegistry(List<SerializerType> serializers, CatalogPort catalogPort) {
        this.serializers = serializers;
        this.catalogPort = catalogPort;
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
                log.error(catalogPort.getMessage("TCH_017"), mediaType);
                return null;
            }
        });
    }
}
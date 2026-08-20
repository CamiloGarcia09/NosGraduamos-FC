package co.edu.uco.infraestructure.secondaryadapters.presenter.serializer;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public final class SerializerRegistry {
    private final LoggingPort log;
    private final List<SerializerType> serializers;
    private final CatalogPort catalogPort;
    public SerializerRegistry(List<SerializerType> serializers, CatalogPort catalogPort,
                              LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(SerializerRegistry.class);
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
                log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_017.getCode()), mediaType);
                return null;
            }
        });
    }
}

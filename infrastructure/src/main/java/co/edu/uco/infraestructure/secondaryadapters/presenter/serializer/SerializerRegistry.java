package co.edu.uco.infraestructure.secondaryadapters.presenter.serializer;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
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
            return defaultSerializer.orElseGet(() -> {
                var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_017.getCode());
                log.error(message, mediaType);
                throw CrossWordsException.buildInfrastructure(message,
                        catalogPort.getMessage(MessageCatalogCodeEnum.FUN_023.getCode()), ExceptionType.TECHNICAL);
            });
        });
    }
}

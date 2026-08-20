package co.edu.uco.infraestructure.secondaryadapters.secrets.doppler;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.secret.SecretProviderPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.infraestructure.config.DopplerProperties;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.Map;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.*;
import static co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType.TECHNICAL;

@Component
public final class DopplerProvider implements SecretProviderPort {

    private final LoggingPort log;
    private final DopplerProperties properties;
    private final ObjectMapper mapper;
    private final CatalogPort catalogPort;

    public DopplerProvider(DopplerProperties properties, ObjectMapper mapper, CatalogPort catalogPort,
                           LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(DopplerProvider.class);
        this.properties = properties;
        this.mapper = mapper;
        this.catalogPort = catalogPort;
    }

    @Override
    public Map<String, String> findSecretToken(String secretName) {
        var client = new OkHttpClient();

        var request = new Request.Builder()
                .url(properties.getUrlConfigSecretsGet().formatted(secretName))
                .get()
                .addHeader(REQUEST_GET_HEADER_ACCEPT.toLowerCase(), JSON_SERIALIZER_CONTENT_TYPE)
                .addHeader(REQUEST_GET_HEADER_AUTHORIZATION.toLowerCase(), BEARER_TOKEN.formatted(properties.getToken()))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_030.getCode()).formatted(response.code());
                log.error(message);
                throw CrossWordsException.buildInfrastructure(
                        message,
                        catalogPort.getMessage(MessageCatalogCodeEnum.FUN_025.getCode()),
                        TECHNICAL
                );
            } else {
                var body = response.body();
                if (isNullObject(body)) {
                    var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_029.getCode());
                    log.error(message);
                    throw CrossWordsException.buildInfrastructure(
                            message,
                            catalogPort.getMessage(MessageCatalogCodeEnum.FUN_025.getCode()),
                            TECHNICAL
                    );
                }
                var dopplerFindTokenDTO = mapper.readValue(body.byteStream(), DopplerFindTokenDTO.class);
                return Map.of(
                        DOPPLER_DTO_SECRET_NAME, dopplerFindTokenDTO.getName(),
                        DOPPLER_DTO_PRIVATE_KEY, dopplerFindTokenDTO.getRaw()
                );
            }
        } catch (Exception e) {
            var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_029.getCode());
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(
                    message,
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_025.getCode()),
                    e,
                    TECHNICAL
            );
        }
    }
}

package co.edu.uco.infraestructure.secondaryadapters.external.secrets.impl.doppler;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.secret.CreateTokenSecretPort;
import co.edu.uco.infraestructure.config.DopplerProperties;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
import okhttp3.*;
import org.springframework.stereotype.Component;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.*;

@Component
public final class DopplerCreateToken implements CreateTokenSecretPort {
    private final LoggingPort log;
    private final DopplerProperties properties;
    private final DopplerSecretCacheService cacheService;
    private final CatalogPort catalogPort;
    public DopplerCreateToken(DopplerProperties properties, DopplerSecretCacheService cacheService,
                              CatalogPort catalogPort, LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(DopplerCreateToken.class);
        this.properties = properties;
        this.cacheService = cacheService;
        this.catalogPort = catalogPort;
    }
    @Override
    public void execute(String secretName, String privateKey) {
        var client = new OkHttpClient();
        var mediaType = MediaType.parse(JSON_SERIALIZER_CONTENT_TYPE);
        var body = RequestBody.create(String.format(properties.getRequest(), secretName, secretName, privateKey), mediaType);
        var request = new Request.Builder()
                .url(properties.getUrlConfigSecretsPost())
                .post(body)
                .addHeader(REQUEST_GET_HEADER_ACCEPT.toLowerCase(), JSON_SERIALIZER_CONTENT_TYPE)
                .addHeader(REQUEST_GET_HEADER_CONTENT_TYPE.toLowerCase(), JSON_SERIALIZER_CONTENT_TYPE)
                .addHeader(REQUEST_GET_HEADER_AUTHORIZATION.toLowerCase(), BEARER_TOKEN.formatted(properties.getToken()))
                .build();

        try(Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful()) {
                var message = catalogPort.getMessage("TCH_030").formatted(response.code());
                log.error(message);
                throw CrossWordsException.buildInfrastructure(
                        message,
                        catalogPort.getMessage("FUN_025"),
                        ExceptionType.TECHNICAL
                );
            }
            cacheService.invalidateCache(secretName);
        } catch (Exception e) {
            var message = catalogPort.getMessage("TCH_029");
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(
                    message,
                    catalogPort.getMessage("FUN_025"),
                    e,
                    ExceptionType.TECHNICAL
            );
        }
    }
}

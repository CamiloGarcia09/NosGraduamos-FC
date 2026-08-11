package co.edu.uco.infrastructure.adapter.secondary.external.secrets.impl.doppler;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.secret.CreateTokenSecretPort;
import co.edu.uco.infrastructure.configuration.DopplerProperties;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.exception.enumeration.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;

@Slf4j
@Component
public final class DopplerCreateToken implements CreateTokenSecretPort {
    private final DopplerProperties properties;
    private final DopplerSecretCacheService cacheService;
    private final InMemoryCatalog inMemoryCatalog;
    public DopplerCreateToken(DopplerProperties properties, DopplerSecretCacheService cacheService, InMemoryCatalog inMemoryCatalog) {
        this.properties = properties;
        this.cacheService = cacheService;
        this.inMemoryCatalog = inMemoryCatalog;
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
                var message = inMemoryCatalog.getContent(MessageKeyEnum.TCH_030.getKey()).formatted(response.code());
                log.error(message);
                throw CrossWordsException.buildInfrastructure(
                        message,
                        inMemoryCatalog.getContent(MessageKeyEnum.FUN_025.getKey()),
                        ExceptionType.TECHNICAL
                );
            }
            cacheService.invalidateCache(secretName);
        } catch (Exception e) {
            var message = inMemoryCatalog.getContent(MessageKeyEnum.TCH_029.getKey());
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(
                    message,
                    inMemoryCatalog.getContent(MessageKeyEnum.FUN_025.getKey()),
                    e,
                    ExceptionType.TECHNICAL
            );
        }
    }
}
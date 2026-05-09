package co.edu.uco.infrastructure.adapter.secondary.external.secrets.impl.doppler;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.port.out.secret.FindSecretTokenPort;
import co.edu.uco.infrastructure.configuration.DopplerProperties;
import co.edu.uco.utils.exception.CrossWordsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.Map;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.exception.enumeration.ExceptionType.TECHNICAL;

@Slf4j
@Component
public final class DopplerFindToken implements FindSecretTokenPort {
    private final DopplerProperties properties;
    private final ObjectMapper mapper;
    public DopplerFindToken(DopplerProperties properties, ObjectMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
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

        try(Response response = client.newCall(request).execute()){
            if(!response.isSuccessful()){
                var message = DetailMessageEnum.TCH_030.getContent().formatted(response.code());
                log.error(message);
                throw CrossWordsException.buildInfrastructure(
                        message,
                        DetailMessageEnum.FUN_025.getContent(),
                        TECHNICAL
                );
            }else {
                assert response.body() != null;
                var dopplerFindTokenDTO = mapper.readValue(response.body().byteStream(), DopplerFindTokenDTO.class);
                return Map.of(
                        DOPPLER_DTO_SECRET_NAME, dopplerFindTokenDTO.getName(),
                        DOPPLER_DTO_PRIVATE_KEY, dopplerFindTokenDTO.getRaw()
                );
            }
        }catch (Exception e){
            var message = DetailMessageEnum.TCH_029.getContent();
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(
                    message,
                    DetailMessageEnum.FUN_025.getContent(),
                    e,
                    TECHNICAL
            );
        }
    }
}

package co.edu.uco.infraestructure.secondaryadapters.parameter;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.ParameterData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.parameter.ParameterPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "components.parameter", name = "enabled", havingValue = "false", matchIfMissing = true)
public final class ParameterAdapter implements ParameterPort {

    private static final String BURNED_PARAMETER_TOKEN_EXPIRATION = "token.expiration.minutes";
    private static final String BURNED_PARAMETER_SEARCH_MAX_RESULTS = "message.search.max.results";

    private final LoggingPort log;
    private final Map<String, ParameterData> burnedParameters;

    public ParameterAdapter(LoggingPortFactory loggingPortFactory) {
        this.log = loggingPortFactory.getLogger(ParameterAdapter.class);
        this.burnedParameters = new HashMap<>();
        burnedParameters.put(BURNED_PARAMETER_TOKEN_EXPIRATION, new ParameterData(
                UUID.fromString("223e4567-e89b-12d3-a456-426614174100"),
                messageOf("PARAM_TOKEN_EXPIRATION"),
                BURNED_PARAMETER_TOKEN_EXPIRATION,
                "Minutos de vigencia de un token. VALOR-QUEMADO: 30"
        ));
        burnedParameters.put(BURNED_PARAMETER_SEARCH_MAX_RESULTS, new ParameterData(
                UUID.fromString("223e4567-e89b-12d3-a456-426614174101"),
                messageOf("PARAM_SEARCH_MAX_RESULTS"),
                BURNED_PARAMETER_SEARCH_MAX_RESULTS,
                "Maximo de resultados por busqueda. VALOR-QUEMADO: 50"
        ));
    }

    @Override
    public Optional<ParameterData> findParameter(String name) {
        log.info("Consultando parametro quemado [%s]".formatted(name));
        return Optional.ofNullable(burnedParameters.get(name));
    }

    private MessageData messageOf(String code) {
        var message = MessageData.build();
        message.setCode(code);
        return message;
    }
}
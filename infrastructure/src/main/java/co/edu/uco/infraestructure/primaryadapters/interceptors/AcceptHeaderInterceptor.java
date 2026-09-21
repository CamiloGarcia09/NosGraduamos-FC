package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.infraestructure.secondaryadapters.presenter.rest.ErrorResponseFactory;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.REQUEST_GET_HEADER_ACCEPT;

@Component
public final class AcceptHeaderInterceptor implements HandlerInterceptor {

    private final LoggingPort log;
    private final SerializerRegistry serializerRegistry;
    private final CatalogPort catalogPort;

    public AcceptHeaderInterceptor(SerializerRegistry serializerRegistry, CatalogPort catalogPort,
                                   LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(AcceptHeaderInterceptor.class);
        this.serializerRegistry = serializerRegistry;
        this.catalogPort = catalogPort;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
            throws Exception {
        var acceptHeader = Optional.ofNullable(request.getHeader(REQUEST_GET_HEADER_ACCEPT))
                .orElse(MediaType.APPLICATION_JSON_VALUE);

        var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);

        if (!serializer.supports(acceptHeader)) {
            var errorMessage = String.format(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_022.getCode()), acceptHeader);
            var errorResponse = ErrorResponseFactory.build(
                    ErrorResponseFactory.codeForStatus(HttpStatus.NOT_ACCEPTABLE.value()),
                    errorMessage,
                    request
            );
            var formattedError = serializer.serialize(errorResponse);
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedError);
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_023.getCode()), formattedError);
            return false;
        }
        return true;
    }
}

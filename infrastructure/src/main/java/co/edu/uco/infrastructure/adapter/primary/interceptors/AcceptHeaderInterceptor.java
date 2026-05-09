package co.edu.uco.infrastructure.adapter.primary.interceptors;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.port.out.Response;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.SerializerRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.REQUEST_GET_HEADER_ACCEPT;
import static co.edu.uco.utils.helper.UtilObject.isNullObject;

@Component
@Slf4j
public final class AcceptHeaderInterceptor implements HandlerInterceptor {
    private final SerializerRegistry serializerRegistry;
    public AcceptHeaderInterceptor(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        var acceptHeader = Optional.ofNullable(request.getHeader(REQUEST_GET_HEADER_ACCEPT))
                .orElse(MediaType.APPLICATION_JSON_VALUE);

        var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);

        if (isNullObject(serializer) || !serializer.supports(acceptHeader)) {
            var errorMessage = String.format(DetailMessageEnum.TCH_022.getContent(), acceptHeader);
            var errorResponse = new Response<String>(List.of(), List.of(errorMessage));
            assert !isNullObject(serializer);
            var formattedError = serializer.serialize(errorResponse);
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedError);
            log.error(DetailMessageEnum.TCH_023.getContent(), formattedError);
            return false;
        }
        return true;
    }
}
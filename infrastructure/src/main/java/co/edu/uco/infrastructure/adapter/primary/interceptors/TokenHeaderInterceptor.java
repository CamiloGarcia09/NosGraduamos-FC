package co.edu.uco.infrastructure.adapter.primary.interceptors;

import co.edu.uco.core.application.facade.token.FindEnvironmentIdTokenUseCaseFacade;
import co.edu.uco.core.application.facade.token.VerifyAccessUseCaseFacade;
import co.edu.uco.core.domain.port.out.Response;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.SerializerRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.List;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;

@Component
@Slf4j
public final class TokenHeaderInterceptor implements HandlerInterceptor {
    private final SerializerRegistry serializerRegistry;
    private final VerifyAccessUseCaseFacade verifyAccessUseCaseFacade;
    private final FindEnvironmentIdTokenUseCaseFacade findEnvironmentIdTokenUseCaseFacade;
    private final CatalogPort catalogPort;
    public TokenHeaderInterceptor(SerializerRegistry serializerRegistry,
                                  VerifyAccessUseCaseFacade verifyAccessUseCaseFacade,
                                  FindEnvironmentIdTokenUseCaseFacade findEnvironmentIdTokenUseCaseFacade,
                                  CatalogPort catalogPort) {
        this.serializerRegistry = serializerRegistry;
        this.verifyAccessUseCaseFacade = verifyAccessUseCaseFacade;
        this.findEnvironmentIdTokenUseCaseFacade = findEnvironmentIdTokenUseCaseFacade;
        this.catalogPort = catalogPort;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        var token = request.getHeader(REQUEST_GET_HEADER_TOKEN);
        var acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);

        if (isEmptyOrNull(token)) {
            sendErrorResponse(response, acceptHeader, catalogPort.getMessage("TCH_032"));
            return false;
        }

        if (!verifyAccessUseCaseFacade.execute(token)) {
            sendErrorResponse(response, acceptHeader, catalogPort.getMessage("TCH_031"));
            return false;
        }

        var environmentId = findEnvironmentIdTokenUseCaseFacade.execute(token);
        request.setAttribute(ENVIRONMENT_ID_ATTRIBUTE, environmentId);
        return true;
    }
    private void sendErrorResponse(HttpServletResponse response, String acceptHeader, String errorMessage) throws IOException {
        var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);
        Response<String> errorResponse = new Response<>(List.of(), List.of(errorMessage));
        String formattedError = serializer.serialize(errorResponse);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(serializer.getSupportedContentType());
        response.getWriter().write(formattedError);
    }
}
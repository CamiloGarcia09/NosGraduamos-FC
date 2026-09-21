package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.primaryports.facade.token.FindEnvironmentIdTokenUseCaseFacade;
import co.edu.uco.application.primaryports.facade.token.VerifyAccessUseCaseFacade;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.infraestructure.secondaryadapters.presenter.rest.ErrorResponseFactory;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.*;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

@Component
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
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        var token = request.getHeader(REQUEST_GET_HEADER_TOKEN);
        var acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);

        if (isEmptyOrNull(token)) {
            sendErrorResponse(request, response, acceptHeader,
                    HttpStatus.UNAUTHORIZED.value(),
                    catalogPort.getMessage(MessageCatalogCodeEnum.TCH_032.getCode()));
            return false;
        }

        if (!isValidToken(token, request, response, acceptHeader)) {
            return false;
        }

        var environmentId = findEnvironmentIdTokenUseCaseFacade.execute(token);
        request.setAttribute(ENVIRONMENT_ID_ATTRIBUTE, environmentId);
        return true;
    }

    private boolean isValidToken(String token, HttpServletRequest request, HttpServletResponse response,
                                 String acceptHeader) throws IOException {
        try {
            if (!verifyAccessUseCaseFacade.execute(token)) {
                sendErrorResponse(request, response, acceptHeader,
                        HttpStatus.FORBIDDEN.value(),
                        catalogPort.getMessage(MessageCatalogCodeEnum.TCH_031.getCode()));
                return false;
            }
            return true;
        } catch (BusinessRuleException ex) {
            var message = Optional.ofNullable(ex.getUserMessage())
                    .filter(msg -> !msg.isEmpty())
                    .orElseGet(() -> catalogPort.getMessage(MessageCatalogCodeEnum.TCH_031.getCode()));
            sendErrorResponse(request, response, acceptHeader, HttpStatus.FORBIDDEN.value(), message);
            return false;
        }
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                   String acceptHeader, int status, String errorMessage) throws IOException {
        var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);
        var errorResponse = ErrorResponseFactory.build(
                ErrorResponseFactory.codeForStatus(status),
                errorMessage,
                request
        );
        String formattedError = serializer.serialize(errorResponse);
        response.setStatus(status);
        response.setContentType(serializer.getSupportedContentType());
        response.getWriter().write(formattedError);
    }
}
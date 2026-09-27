package co.edu.uco.infraestructure.primaryadapters.interceptors;

import co.edu.uco.application.secondaryports.ErrorResponse;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.security.ExternalIdentityResolverPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import co.edu.uco.infraestructure.secondaryadapters.presenter.rest.ErrorResponseFactory;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.EXTERNAL_IDENTITY_ATTRIBUTE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.REQUEST_GET_HEADER_ACCEPT;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.REQUEST_GET_HEADER_AUTHORIZATION;

@Component
@ConditionalOnProperty(
        prefix = "components.security.simulated",
        name = "enabled",
        havingValue = "true"
)
public final class ExternalIdentityInterceptor implements HandlerInterceptor {

    private static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer ([^\\s]+)$", Pattern.CASE_INSENSITIVE);

    private final ExternalIdentityResolverPort identityResolver;
    private final SerializerRegistry serializerRegistry;
    private final CatalogPort catalogPort;

    public ExternalIdentityInterceptor(ExternalIdentityResolverPort identityResolver,
                                       SerializerRegistry serializerRegistry,
                                       CatalogPort catalogPort) {
        this.identityResolver = identityResolver;
        this.serializerRegistry = serializerRegistry;
        this.catalogPort = catalogPort;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws IOException {
        String authorization = request.getHeader(REQUEST_GET_HEADER_AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            return true;
        }

        Matcher matcher = BEARER_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            sendUnauthorized(request, response);
            return false;
        }

        try {
            request.setAttribute(EXTERNAL_IDENTITY_ATTRIBUTE, identityResolver.resolve(matcher.group(1)));
            return true;
        } catch (UnauthorizedException exception) {
            sendUnauthorized(request, response);
            return false;
        }
    }

    private void sendUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);
        var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);
        int status = HttpStatus.UNAUTHORIZED.value();
        ErrorResponse errorResponse = ErrorResponseFactory.build(
                ErrorResponseFactory.codeForStatus(status),
                catalogPort.getMessage(MessageCatalogCodeEnum.TCH_031.getCode()),
                request
        );
        response.setStatus(status);
        response.setContentType(serializer.getSupportedContentType());
        response.getWriter().write(serializer.serialize(errorResponse));
    }
}

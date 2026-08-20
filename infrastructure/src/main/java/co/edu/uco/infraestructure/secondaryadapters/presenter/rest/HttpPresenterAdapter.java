package co.edu.uco.infraestructure.secondaryadapters.presenter.rest;

import co.edu.uco.application.secondaryports.Response;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.infraestructure.secondaryadapters.presenter.serializer.SerializerRegistry;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.REQUEST_GET_HEADER_ACCEPT;

@RestControllerAdvice
public final class HttpPresenterAdapter<T> implements PresenterPort<T> {

    private final LoggingPort log;
    private final SerializerRegistry serializerRegistry;
    private final CatalogPort catalogPort;

    public HttpPresenterAdapter(SerializerRegistry serializerRegistry, CatalogPort catalogPort,
                                LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(HttpPresenterAdapter.class);
        this.serializerRegistry = serializerRegistry;
        this.catalogPort = catalogPort;
    }

    @Override
    public void presentRestSuccess(
            List<T> dto,
            HttpServletRequest  request,
            HttpServletResponse response
    ) {
        try {
            var acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);
            var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);

            var responseBody = new Response<>(dto, Collections.emptyList());
            var formattedResponse = serializer.serialize(responseBody);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedResponse);
            log.info(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_021.getCode()), formattedResponse);

        } catch (CrossWordsException | IOException ex) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_016.getCode()), ex);
        }
    }

    @ExceptionHandler(CrossWordsException.class)
    public void presentCrossWordsException(
            CrossWordsException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            var acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);
            var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);

            var message = Optional.ofNullable(ex.getUserMessage())
                    .filter(msg -> !msg.isEmpty())
                    .orElseGet(() -> {
                        log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_016.getCode()), ex);
                        return catalogPort.getMessage(MessageCatalogCodeEnum.FUN_023.getCode());
                    });
            var responseError = new Response<>(List.of(), List.of(message));
            var formattedResponse = serializer.serialize(responseError);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedResponse);
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_020.getCode()), formattedResponse);
        } catch (IOException | CrossWordsException exception) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_019.getCode()), exception);
            throw exception;
        }
    }

    @ExceptionHandler(Exception.class)
    public void handleGeneralException(
            Exception ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            var acceptHeader = request.getHeader(REQUEST_GET_HEADER_ACCEPT);
            var serializer = serializerRegistry.getSerializerForMediaType(acceptHeader);

            var responseError = new Response<>(List.of(), List.of(ex.getMessage()));
            var formattedResponse = serializer.serialize(responseError);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedResponse);
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_020.getCode()), formattedResponse);
        } catch (IOException ioEx) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.TCH_019.getCode()), ioEx);
        }
    }
}

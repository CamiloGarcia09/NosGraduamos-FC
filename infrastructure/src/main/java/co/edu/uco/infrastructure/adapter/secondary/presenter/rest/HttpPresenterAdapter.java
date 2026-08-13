package co.edu.uco.infrastructure.adapter.secondary.presenter.rest;

import co.edu.uco.core.domain.port.out.Response;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.presenter.PresenterPort;
import co.edu.uco.infrastructure.adapter.secondary.presenter.serializer.SerializerRegistry;
import co.edu.uco.utils.exception.CrossWordsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.REQUEST_GET_HEADER_ACCEPT;

@Slf4j
@RestControllerAdvice
public final class HttpPresenterAdapter<T> implements PresenterPort<T> {
    private final SerializerRegistry serializerRegistry;
    private final CatalogPort catalogPort;
    public HttpPresenterAdapter(SerializerRegistry serializerRegistry, CatalogPort catalogPort) {
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
            log.info(catalogPort.getMessage("TCH_021"), formattedResponse);
        } catch (CrossWordsException | IOException ex) {
            log.error(catalogPort.getMessage("TCH_016"), ex);
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
                        log.error(catalogPort.getMessage("TCH_016"), ex);
                        return catalogPort.getMessage("FUN_023");
                    });
            var responseError = new Response<>(List.of(), List.of(message));
            var formattedResponse = serializer.serialize(responseError);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType(serializer.getSupportedContentType());
            response.getWriter().write(formattedResponse);
            log.error(catalogPort.getMessage("TCH_020"), formattedResponse);
        } catch (IOException | CrossWordsException exception) {
            log.error(catalogPort.getMessage("TCH_019"), exception);
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
            log.error(catalogPort.getMessage("TCH_020"), formattedResponse);
        } catch (IOException ioEx) {
            log.error(catalogPort.getMessage("TCH_019"), ioEx);
        }
    }
}
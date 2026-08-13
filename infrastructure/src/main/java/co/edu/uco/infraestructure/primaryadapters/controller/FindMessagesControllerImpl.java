package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.primaryports.dto.message.TranslatedMessageDTO;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.primaryports.facade.message.FindMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.primaryports.facade.message.TranslateMessageByCodeAndEnvironmentUseCaseFacade;
import co.edu.uco.application.primaryports.facade.message.impl.FindMessagesByEnvironmentFacadeImpl;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.application.secondaryports.repository.SimplePage;
import co.edu.uco.infraestructure.primaryadapters.FindMessagesController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.ENVIRONMENT_ID_ATTRIBUTE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.MESSAGE_CODE_PARAMETER;

@RestController
@RequestMapping("${crosswords.api.path.message}")
@Tag(name = "Consulta de Mensajes", description = "Endpoints para obtener información de mensajes")
final class FindMessagesControllerImpl implements FindMessagesController {
        private final FindMessagesByEnvironmentFacadeImpl findMessagesByEnvironmentFacadeImpl;
        private final FindMessageByCodeAndEnvironmentUseCaseFacade findMessageByCodeAndEnvironmentUseCaseFacade;
        private final TranslateMessageByCodeAndEnvironmentUseCaseFacade translateMessageByCodeAndEnvironmentUseCaseFacade;
        private final PresenterPort<MessageDTO> restPresenter;
        private final PresenterPort<TranslatedMessageDTO> translationPresenter;
        private final PresenterPort<SimplePage<MessageDTO>> restPresenterPage;
        public FindMessagesControllerImpl(
                FindMessagesByEnvironmentFacadeImpl findMessagesByEnvironmentFacadeImpl,
                FindMessageByCodeAndEnvironmentUseCaseFacade findMessageByCodeAndEnvironmentUseCaseFacade,
                TranslateMessageByCodeAndEnvironmentUseCaseFacade translateMessageByCodeAndEnvironmentUseCaseFacade,
                PresenterPort<MessageDTO> restPresenter,
                PresenterPort<TranslatedMessageDTO> translationPresenter,
                PresenterPort<SimplePage<MessageDTO>> restPresenterPage) {
                this.findMessagesByEnvironmentFacadeImpl = findMessagesByEnvironmentFacadeImpl;
                this.findMessageByCodeAndEnvironmentUseCaseFacade = findMessageByCodeAndEnvironmentUseCaseFacade;
                this.translateMessageByCodeAndEnvironmentUseCaseFacade = translateMessageByCodeAndEnvironmentUseCaseFacade;
                this.restPresenter = restPresenter;
                this.translationPresenter = translationPresenter;
                this.restPresenterPage = restPresenterPage;
        }
        @Override
        @GetMapping("${crosswords.api.path.message.environment}")
        @Operation(summary = "Listar mensajes por ambiente", 
                   description = "Retorna una lista paginada de mensajes asociados al ambiente del token actual. " +
                   "El endpoint obtiene el ID del ambiente del token y opcionalmente acepta parámetros de paginación.")
        @ApiResponse(responseCode = "200", description = "Lista de mensajes obtenida correctamente", 
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = SimplePage.class)),
                            @Content(mediaType = "application/yaml", schema = @Schema(implementation = SimplePage.class)),
                            @Content(mediaType = "application/xml", schema = @Schema(implementation = SimplePage.class)),
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = SimplePage.class)),
                            @Content(mediaType = "text/html", schema = @Schema(implementation = SimplePage.class))
                    })
        @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, parámetros inválidos o faltantes")
        @ApiResponse(responseCode = "401", description = "No autorizado, token inválido o expirado")
        @ApiResponse(responseCode = "404", description = "No se encontraron mensajes para el ambiente especificado")
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        @ApiResponse(responseCode = "406", description = "Formato de respuesta no soportado")
        public void findByEnvironmentAndMessage(
                        @Parameter(name = "page", description = "Número de página a consultar (comienza en 1)", example = "1")
                        @RequestParam(value = "page", required = false) String page,
                        @Parameter(name = "size", description = "Cantidad de elementos por página", example = "10")
                        @RequestParam(value = "size", required = false) String size,
                        @Parameter(name = "sort", description = "Dirección de ordenamiento (ASC o DESC)", example = "ASC")
                        @RequestParam(value = "sort", required = false) String sort,
                        @Parameter(name = "columnSort", description = "Campo por el cual ordenar los resultados", example = "code")
                        @RequestParam(value = "columnSort", required = false) String columnSort,
                        @Parameter(name = "Token", description = "Token de autorización", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "your_token_here"))
                        @RequestHeader("Token") String token,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {

                var pageRequestDTO = PageRequestDTO.builder()
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .columnSort(columnSort)
                        .build();
                
                var environmentId = (String) httpServletRequest.getAttribute(ENVIRONMENT_ID_ATTRIBUTE);
                var messageDTOSimplePage = findMessagesByEnvironmentFacadeImpl.execute(environmentId, pageRequestDTO);
                restPresenterPage.presentRestSuccess(List.of(messageDTOSimplePage), httpServletRequest,
                                httpServletResponse);
        }
        @Override
        @GetMapping("${crosswords.api.path.message.code.environment}")
        @Operation(summary = "Buscar mensaje por código y ambiente", description = "Permite obtener el mensaje correspondiente a un código específico en el ambiente asociado al token. "
                        +
                        "El endpoint recibe el parámetro 'messageCode' y utiliza el ID del ambiente obtenido del token.", parameters = {
                                        @Parameter(name = "messageCode", description = "Código del mensaje a buscar", required = true, example = "MSG_001"),
                                        @Parameter(name = "Token", description = "Token de autorización", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "your_token_here")
                        )
                        }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Mensaje encontrado correctamente", content = {
                                                        @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class)),
                                                        @Content(mediaType = "application/yaml", schema = @Schema(implementation = MessageDTO.class)),
                                                        @Content(mediaType = "application/xml", schema = @Schema(implementation = MessageDTO.class)),
                                                        @Content(mediaType = "text/plain", schema = @Schema(implementation = MessageDTO.class)),
                                                        @Content(mediaType = "text/html", schema = @Schema(implementation = MessageDTO.class))
                                        }),
                                        @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, parámetros inválidos o faltantes"),
                                        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado"),
                                        @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
                                        @ApiResponse(responseCode = "406", description = "Formato de respuesta no soportado")
                        })
        public void findByCodeMessageAndEnvironment(
                        @PathVariable(MESSAGE_CODE_PARAMETER) String messageCode,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
                var environmentId = (String) httpServletRequest.getAttribute(ENVIRONMENT_ID_ATTRIBUTE);
                var messageDTO = findMessageByCodeAndEnvironmentUseCaseFacade.execute(messageCode,
                                environmentId);
                restPresenter.presentRestSuccess(List.of(messageDTO), httpServletRequest, httpServletResponse);
        }

        @Override
        @GetMapping("${crosswords.api.path.message.code.translation}")
        @Operation(summary = "Traducir mensaje por codigo y ambiente",
                   description = "Obtiene el mensaje original asociado al ambiente del token y lo traduce dinamicamente al idioma solicitado sin guardar la traduccion en la base de datos.",
                   parameters = {
                           @Parameter(name = "messageCode", description = "Codigo del mensaje a traducir", required = true, example = "MSG_001"),
                           @Parameter(name = "sourceLanguage", description = "Idioma origen. Si no se envia, se detecta automaticamente.", example = "auto"),
                           @Parameter(name = "targetLanguage", description = "Idioma destino de la traduccion", required = true, example = "en"),
                           @Parameter(name = "Token", description = "Token de autorizacion", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "your_token_here"))
                   },
                   responses = {
                           @ApiResponse(responseCode = "200", description = "Mensaje traducido correctamente", content = {
                                   @Content(mediaType = "application/json", schema = @Schema(implementation = TranslatedMessageDTO.class)),
                                   @Content(mediaType = "application/yaml", schema = @Schema(implementation = TranslatedMessageDTO.class)),
                                   @Content(mediaType = "application/xml", schema = @Schema(implementation = TranslatedMessageDTO.class)),
                                   @Content(mediaType = "text/plain", schema = @Schema(implementation = TranslatedMessageDTO.class)),
                                   @Content(mediaType = "text/html", schema = @Schema(implementation = TranslatedMessageDTO.class))
                           }),
                           @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
                           @ApiResponse(responseCode = "401", description = "No autorizado"),
                           @ApiResponse(responseCode = "404", description = "Mensaje no encontrado o traduccion no disponible"),
                           @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
                           @ApiResponse(responseCode = "406", description = "Formato de respuesta no soportado")
                   })
        public void translateByCodeMessageAndEnvironment(
                        @PathVariable(MESSAGE_CODE_PARAMETER) String messageCode,
                        @RequestParam(value = "sourceLanguage", required = false, defaultValue = "auto") String sourceLanguage,
                        @RequestParam(value = "targetLanguage") String targetLanguage,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
                var environmentId = (String) httpServletRequest.getAttribute(ENVIRONMENT_ID_ATTRIBUTE);
                var translatedMessageDTO = translateMessageByCodeAndEnvironmentUseCaseFacade.execute(
                                messageCode,
                                environmentId,
                                sourceLanguage,
                                targetLanguage);
                translationPresenter.presentRestSuccess(List.of(translatedMessageDTO), httpServletRequest,
                                httpServletResponse);
        }
}

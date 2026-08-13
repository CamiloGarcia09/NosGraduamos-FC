package co.edu.uco.infraestructure.primaryadapters.controller;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.primaryports.facade.token.CreateTokenUseCaseFacade;
import co.edu.uco.application.secondaryports.presenter.PresenterPort;
import co.edu.uco.infraestructure.primaryadapters.CreateTokenController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_ID;
@RestController
@RequestMapping("${crosswords.api.path.message}")
@Tag(name = "Gestión de Tokens", description = "Endpoints para la gestión de tokens")
final class CreateTokenControllerImpl implements CreateTokenController {
    private final CreateTokenUseCaseFacade createTokenUseCaseFacade;
    private final PresenterPort<String> restPresenter;
    public CreateTokenControllerImpl(CreateTokenUseCaseFacade createTokenUseCaseFacade, PresenterPort<String> restPresenter) {
        this.createTokenUseCaseFacade = createTokenUseCaseFacade;
        this.restPresenter = restPresenter;
    }
    @PostMapping("${crosswords.api.path.token.application}")
    @Operation(summary = "Crear token de aplicación", 
              description = "Crea un nuevo token para una aplicación específica. El token generado puede ser utilizado para autenticar las solicitudes.")
    @Parameter(name = "id", description = "Identificador único de la aplicación", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @ApiResponse(responseCode = "200", description = "Token creado exitosamente", 
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "application/yaml", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "text/html", schema = @Schema(implementation = String.class))
                })
    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, datos inválidos")
    @ApiResponse(responseCode = "404", description = "Aplicación no encontrada")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @ApiResponse(responseCode = "406", description = "Formato de respuesta no soportado")
    @Override
    public void createToken(
            @RequestBody CreateTokenDTO tokenDTO,
            @PathVariable(FIELD_ID) String id,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        var result = createTokenUseCaseFacade.execute(tokenDTO, id);
        restPresenter.presentRestSuccess(List.of(result), httpServletRequest, httpServletResponse);
    }
}
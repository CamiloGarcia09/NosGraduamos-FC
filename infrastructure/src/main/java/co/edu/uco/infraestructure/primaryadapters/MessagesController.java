package co.edu.uco.infraestructure.primaryadapters;

import co.edu.uco.application.primaryports.dto.message.TranslateMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.MESSAGE_CODE_PARAMETER;

@RequestMapping("${crosswords.api.path.messages}")
public interface MessagesController {

    @GetMapping(
            params = "!code",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void findByEnvironmentAndMessage(
            @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "columnSort", required = false) String columnSort,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );

    @GetMapping(
            params = "code",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void findByCodeMessageAndEnvironment(
            @RequestParam("code") String messageCode,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );

    @PostMapping(
            value = "/{messageCode}/translations",
            consumes = "application/json",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void translateByCodeMessageAndEnvironment(
            @PathVariable(MESSAGE_CODE_PARAMETER) String messageCode,
            @RequestBody TranslateMessageDTO translateMessageDTO,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );
}

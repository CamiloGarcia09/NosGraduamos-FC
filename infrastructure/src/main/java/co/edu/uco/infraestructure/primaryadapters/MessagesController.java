package co.edu.uco.infraestructure.primaryadapters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.MESSAGE_CODE_PARAMETER;

@RequestMapping("${crosswords.api.path.message}")
public interface MessagesController {

    @GetMapping(
            value = "${crosswords.api.path.message.environment}",
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
            value = "${crosswords.api.path.message.code.environment}",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void findByCodeMessageAndEnvironment(
            @PathVariable(MESSAGE_CODE_PARAMETER) String messageCode,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );

    @GetMapping(
            value = "${crosswords.api.path.message.code.translation}",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void translateByCodeMessageAndEnvironment(
            @PathVariable(MESSAGE_CODE_PARAMETER) String messageCode,
            @RequestParam(value = "sourceLanguage", required = false, defaultValue = "auto") String sourceLanguage,
            @RequestParam("targetLanguage") String targetLanguage,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );
}

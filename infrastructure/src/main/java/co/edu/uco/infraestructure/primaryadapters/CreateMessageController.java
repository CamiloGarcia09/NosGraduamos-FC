package co.edu.uco.infraestructure.primaryadapters;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${crosswords.api.path.message}")
public interface CreateMessageController {

    @PostMapping(
            value = "/message",
            consumes = "application/json",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void createMessage(
            @RequestBody CreateMessageDTO createMessageDTO,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );
}

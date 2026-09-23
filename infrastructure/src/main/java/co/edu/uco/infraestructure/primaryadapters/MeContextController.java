package co.edu.uco.infraestructure.primaryadapters;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/messageucolab/v1/me")
public interface MeContextController {

    @GetMapping(value = "/contexts", produces = "application/json")
    void getAvailableContexts(HttpServletRequest request, HttpServletResponse response);

    @GetMapping(value = "/context", produces = "application/json")
    void getActiveContext(HttpServletRequest request, HttpServletResponse response);

    @PutMapping(value = "/context", consumes = "application/json", produces = "application/json")
    void selectActiveContext(@RequestBody SelectActiveContextDTO context,
                             HttpServletRequest request, HttpServletResponse response);
}

package co.edu.uco.infraestructure.primaryadapters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/messageucolab/v1/catalog")
public interface CatalogController {

    @GetMapping(
            value = "/applications",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getApplications(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    @GetMapping(
            value = "/applications/{applicationId}/environments",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getEnvironmentsByApplication(
            @PathVariable("applicationId") String applicationId,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );

    @GetMapping(
            value = "/applications/{applicationId}/functionalities",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getFunctionalitiesByApplication(
            @PathVariable("applicationId") String applicationId,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    );

    @GetMapping(
            value = "/message-types",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getMessageTypes(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    @GetMapping(
            value = "/message-categories",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getMessageCategories(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    @GetMapping(
            value = "/message-states",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getMessageStates(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    @GetMapping(
            value = "/message-environment-states",
            produces = {"application/json", "application/yaml", "application/xml", "text/plain", "text/html"}
    )
    void getMessageEnvironmentStates(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}

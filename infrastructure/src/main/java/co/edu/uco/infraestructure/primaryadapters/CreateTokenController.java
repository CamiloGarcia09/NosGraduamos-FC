package co.edu.uco.infraestructure.primaryadapters;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CreateTokenController {
    void createToken(CreateTokenDTO tokenDTO, String applicationId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
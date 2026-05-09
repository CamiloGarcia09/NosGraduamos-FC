package co.edu.uco.infrastructure.adapter.primary;

import co.edu.uco.core.application.dto.token.CreateTokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CreateTokenController {
    void createToken(CreateTokenDTO tokenDTO, String applicationId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
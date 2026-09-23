package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;

public record TokenValidationContext(CreateTokenDTO dto, String applicationId) {
}
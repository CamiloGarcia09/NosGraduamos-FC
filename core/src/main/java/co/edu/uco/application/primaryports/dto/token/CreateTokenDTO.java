package co.edu.uco.application.primaryports.dto.token;

import lombok.*;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@Builder
public final class CreateTokenDTO {
    private String expirationDate;
    private String environmentId;
    public CreateTokenDTO() {
        setExpirationDate(EMPTY);
        setEnvironmentId(EMPTY);
    }
    public CreateTokenDTO(String expirationDate, String environmentId) {
        setExpirationDate(expirationDate);
        setEnvironmentId(environmentId);
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = trim(expirationDate);
    }
    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }

    // Explicit getters to avoid Lombok/annotation-processing issues during build
    public String getExpirationDate() {
        return expirationDate;
    }

    public String getEnvironmentId() {
        return environmentId;
    }
}

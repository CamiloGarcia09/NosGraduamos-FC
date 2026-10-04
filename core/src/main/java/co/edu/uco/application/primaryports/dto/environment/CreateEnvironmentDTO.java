package co.edu.uco.application.primaryports.dto.environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@Builder
@AllArgsConstructor
public final class CreateEnvironmentDTO {

    private String applicationId;
    private String typeId;
    private String stateId;

    public CreateEnvironmentDTO() {
        setApplicationId(EMPTY);
        setTypeId(EMPTY);
        setStateId(EMPTY);
    }

    public void setApplicationId(String applicationId) { this.applicationId = trim(applicationId); }
    public void setTypeId(String typeId) { this.typeId = trim(typeId); }
    public void setStateId(String stateId) { this.stateId = trim(stateId); }
}

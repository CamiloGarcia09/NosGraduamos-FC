package co.edu.uco.application.primaryports.dto.functionality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@Builder
@AllArgsConstructor
public final class CreateFunctionalityDTO {

    private String name;
    private String applicationId;
    private String startDate;
    private String endDate;
    private String stateId;

    public CreateFunctionalityDTO() {
        setName(EMPTY);
        setApplicationId(EMPTY);
        setStartDate(EMPTY);
        setEndDate(EMPTY);
        setStateId(EMPTY);
    }

    public void setName(String name) { this.name = trim(name); }
    public void setApplicationId(String applicationId) { this.applicationId = trim(applicationId); }
    public void setStartDate(String startDate) { this.startDate = trim(startDate); }
    public void setEndDate(String endDate) { this.endDate = trim(endDate); }
    public void setStateId(String stateId) { this.stateId = trim(stateId); }
}
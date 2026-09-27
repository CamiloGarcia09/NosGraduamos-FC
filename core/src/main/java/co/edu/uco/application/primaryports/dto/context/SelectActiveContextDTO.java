package co.edu.uco.application.primaryports.dto.context;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@Builder
@NoArgsConstructor
public final class SelectActiveContextDTO {

    private String organizationId;
    private String applicationId;
    private String environmentId;

    public SelectActiveContextDTO(final String organizationId, final String applicationId,
                                  final String environmentId) {
        setOrganizationId(organizationId);
        setApplicationId(applicationId);
        setEnvironmentId(environmentId);
    }

    public void setOrganizationId(final String organizationId) {
        this.organizationId = trim(organizationId);
    }

    public void setApplicationId(final String applicationId) {
        this.applicationId = trim(applicationId);
    }

    public void setEnvironmentId(final String environmentId) {
        this.environmentId = trim(environmentId);
    }
}

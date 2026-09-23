package co.edu.uco.application.primaryports.dto.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ActiveContextDTO {

    private String organizationId;
    private String applicationId;
    private String environmentId;
    private LocalDateTime updatedAt;
}

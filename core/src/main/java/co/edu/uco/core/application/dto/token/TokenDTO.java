package co.edu.uco.core.application.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class TokenDTO {
    private String id;
    private String secretName;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private UUID environmentId;
}
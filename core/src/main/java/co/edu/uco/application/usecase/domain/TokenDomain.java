package co.edu.uco.application.usecase.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
@NoArgsConstructor
public final class TokenDomain {
    private String id;
    private String secretName;
    private LocalDateTime expirationDate;
    private UUID environmentId;
    private LocalDateTime creationDate;
    private UUID stateId;
    public TokenDomain(String id,LocalDateTime creationDate ,LocalDateTime expirationDate, UUID environmentId, String secretName, UUID stateId) {
        setId(id);
        setSecretName(secretName);
        setExpirationDate(expirationDate);
        setEnvironmentId(environmentId);
        setCreationDate(creationDate);
        setStateId(stateId);
    }
    public TokenDomain create(String id,LocalDateTime creationDate, LocalDateTime expirationDate, UUID environmentId, String secretName, UUID tokenStateDataId) {return new TokenDomain(id, creationDate,expirationDate, environmentId, secretName, tokenStateDataId);}
    public void setExpirationDate(LocalDateTime expirationDate) {this.expirationDate = getDefaultTimeIfNull(expirationDate);}
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setEnvironmentId(UUID environmentId) {
        this.environmentId = getDefaultUUID(environmentId);
    }
    public void setCreationDate(LocalDateTime creationDate) {this.creationDate = getDefaultTimeIfNull(creationDate);}
    public void setSecretName(String secretName) {
        this.secretName = trim(secretName);
    }
    public void setStateId(UUID stateId) { this.stateId = getDefaultUUID(stateId);}
}
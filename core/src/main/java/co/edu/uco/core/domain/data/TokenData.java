package co.edu.uco.core.domain.data;

import co.edu.uco.utils.helper.UtilDate;
import co.edu.uco.utils.helper.UtilText;
import lombok.Getter;

import java.time.LocalDateTime;

import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
public final class TokenData {
    private String id;
    private String secretName;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private String environmentId;
    private String stateId;
    public TokenData(String id, LocalDateTime creationDate, LocalDateTime expirationDate, String environmentId, String secretName, String stateId) {
        setId(id);
        setSecretName(secretName);
        setCreationDate(creationDate);
        setExpirationDate(expirationDate);
        setEnvironmentId(environmentId);
        setStateId(stateId);
    }
    public TokenData() {
        setId(UtilText.EMPTY);
        setSecretName(UtilText.EMPTY);
        setCreationDate(UtilDate.TIME);
        setExpirationDate(UtilDate.TIME);
        setEnvironmentId(UtilText.EMPTY);
        setStateId(UtilText.EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = UtilDate.getDefaultTimeIfNull(creationDate);
    }
    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = UtilDate.getDefaultTimeIfNull(expirationDate);
    }
    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }
    public void setStateId(String stateId) {
        this.stateId = UtilText.trim(stateId);
    }
    public void setSecretName(String secretName) {
        this.secretName = trim(secretName);
    }
}
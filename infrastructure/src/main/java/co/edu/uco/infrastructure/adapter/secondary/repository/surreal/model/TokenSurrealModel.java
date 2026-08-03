package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.model;

import lombok.Getter;

import java.time.LocalDateTime;

import static co.edu.uco.utils.helper.UtilDate.TIME;
import static co.edu.uco.utils.helper.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
public final class TokenSurrealModel {

    private String id;
    private String secretName;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private String environmentId;
    private String stateId;

    public TokenSurrealModel(String id, String secretName, LocalDateTime creationDate,
                             LocalDateTime expirationDate, String environmentId, String stateId) {
        setId(id);
        setSecretName(secretName);
        setCreationDate(creationDate);
        setExpirationDate(expirationDate);
        setEnvironmentId(environmentId);
        setStateId(stateId);
    }

    public TokenSurrealModel() {
        setId(EMPTY);
        setSecretName(EMPTY);
        setCreationDate(TIME);
        setExpirationDate(TIME);
        setEnvironmentId(EMPTY);
        setStateId(EMPTY);
    }

    public void setId(String id) { this.id = trim(id); }

    public void setSecretName(String secretName) { this.secretName = trim(secretName); }

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = getDefaultTimeIfNull(creationDate); }

    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = getDefaultTimeIfNull(expirationDate); }

    public void setEnvironmentId(String environmentId) { this.environmentId = trim(environmentId); }

    public void setStateId(String stateId) { this.stateId = trim(stateId); }

    public static TokenSurrealModel build() { return new TokenSurrealModel(); }
}

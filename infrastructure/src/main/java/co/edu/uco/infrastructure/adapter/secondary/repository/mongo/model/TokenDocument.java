package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilDate.TIME;
import static co.edu.uco.utils.helper.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
@Document(collection = COLLECTION_TOKEN)
public final class TokenDocument {
    @Id
    @Field(FIELD_ID)
    private String id;
    @Field(value = FIELD_CREATION_DATE, targetType = FieldType.INT64)
    private LocalDateTime creationDate;
    @Field(value = FIELD_EXPIRATION_DATE, targetType = FieldType.INT64)
    private LocalDateTime expirationDate;
    @Field(FIELD_ENVIRONMENT_ID)
    private String environmentId;
    @Field(FIELD_SECRET_NAME)
    private String secretName;
    @Field(FIELD_STATE_ID)
    private String stateId;
    public TokenDocument(String id, LocalDateTime creationDate, LocalDateTime expirationDate,
                         String environmentId, String secretName, String stateId) {
        setId(id);
        setCreationDate(creationDate);
        setExpirationDate(expirationDate);
        setEnvironmentId(environmentId);
        setSecretName(secretName);
        setStateId(stateId);
    }
    public TokenDocument() {
        setId(EMPTY);
        setCreationDate(TIME);
        setExpirationDate(TIME);
        setEnvironmentId(EMPTY);
        setSecretName(EMPTY);
        setStateId(EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = getDefaultTimeIfNull(creationDate);}
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = getDefaultTimeIfNull(expirationDate);}
    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }
    public void setSecretName(String secretName) {
        this.secretName = trim(secretName);
    }
    public void setStateId(String stateId) {this.stateId = trim(stateId);}
    public static TokenDocument build(){return new TokenDocument();}
}
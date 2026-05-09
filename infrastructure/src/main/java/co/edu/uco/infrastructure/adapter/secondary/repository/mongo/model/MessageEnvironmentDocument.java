package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import jakarta.persistence.Id;
import lombok.Getter;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = COLLECTION_MESSAGE_ENVIRONMENT)
@ToString
public final class MessageEnvironmentDocument {
    @Id
    @Field(FIELD_MESSAGE_ENVIRONMENT_ID)
    private String id;
    @Field(FIELD_MESSAGE)
    private MessageDocument message;
    @Field(FIELD_ENVIRONMENT_ID)
    private String environmentId;
    private MessageEnvironmentStateDocument status;
    public MessageEnvironmentDocument(String id, MessageDocument message, String environmentId) {
        setId(id);
        setMessage(message);
        setEnvironmentId(environmentId);
    }
    public MessageEnvironmentDocument() {
        setId(EMPTY);
        setMessage(MessageDocument.build());
        setEnvironmentId(EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setMessage(MessageDocument message) {this.message = getDefaultIsNullObject(message, MessageDocument.build());}
    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }
    public void setStatus(MessageEnvironmentStateDocument status) { this.status = getDefaultIsNullObject(status, MessageEnvironmentStateDocument.build());}
    public static MessageEnvironmentDocument build(){return new MessageEnvironmentDocument();}
}
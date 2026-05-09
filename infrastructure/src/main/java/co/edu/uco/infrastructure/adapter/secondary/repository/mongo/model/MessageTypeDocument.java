package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_ID;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilText.EMPTY;

@Getter
public final class MessageTypeDocument {
    @Field(FIELD_ID)
    private String id;
    private String name;
    public MessageTypeDocument(String id, String name) {
        setId(id);
        setName(name);
    }
    public MessageTypeDocument() {
        setId(EMPTY);
        setName(EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static MessageTypeDocument build(){return new MessageTypeDocument();}
}
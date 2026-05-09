package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.FIELD_ID;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
public final class MessageCategoryDocument {
    @Field(FIELD_ID)
    private String id;
    private String name;
    public MessageCategoryDocument(String id, String name) {
        setId(id);
        setName(name);
    }
    public MessageCategoryDocument() {
        setId(EMPTY);
        setName(EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static  MessageCategoryDocument build(){return new MessageCategoryDocument();}
}
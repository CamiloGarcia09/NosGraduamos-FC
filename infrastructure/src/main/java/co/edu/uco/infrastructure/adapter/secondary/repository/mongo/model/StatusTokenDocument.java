package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
@Document(collection = COLLECTION_TOKEN_STATE)
public final class StatusTokenDocument {
    @Id
    @Field(FIELD_ID)
    private String id;
    @Field(FIELD_NAME)
    private String name;
    public StatusTokenDocument() {
        setId(EMPTY);
        setName(EMPTY);
    }
    public StatusTokenDocument(String id, String name) {
        setId(id);
        setName(name);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static StatusTokenDocument build() {
        return new StatusTokenDocument();
    }
}
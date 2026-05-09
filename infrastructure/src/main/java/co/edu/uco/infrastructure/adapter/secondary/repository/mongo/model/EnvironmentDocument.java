package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import co.edu.uco.utils.helper.UtilText;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilText.EMPTY;

@Getter
@Document(COLLECTION_ENVIRONMENT)
public final class EnvironmentDocument {
    @Id
    @Field(FIELD_ID)
    private String id;
    private String name;
    @Field(FIELD_APPLICATION_ID)
    private String application;
    @Field(FIELD_TYPE_ID)
    private String type;
    @Field(FIELD_STATE_ID)
    private String status;
    public EnvironmentDocument(String id, String name, String application, String type, String status) {
        setId(id);
        setName(name);
        setApplication(application);
        setType(type);
        setStatus(status);
    }
    public EnvironmentDocument() {
        setId(EMPTY);
        setName(EMPTY);
        setApplication(EMPTY);
        setType(EMPTY);
        setStatus(EMPTY);
    }
    public void setId(String id) {
        this.id = UtilText.trim(id);
    }
    public void setName(String name) {
        this.name = UtilText.trim(name);
    }
    public void setApplication(String application) {this.application = UtilText.trim(application);}
    public void setType(String type) {this.type = UtilText.trim(type);}
    public void setStatus(String status) {this.status = UtilText.trim(status);}
    public static EnvironmentDocument build() {
        return new EnvironmentDocument();
    }
}
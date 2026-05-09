package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.COLLECTION_PARAMETER;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
@Document(collection = COLLECTION_PARAMETER)
public final class ParameterDocument {
    @Id
    private String id;
    private String message;
    private String name;
    private String description;
    public ParameterDocument(String id, String message, String name, String description){
        setId(id);
        setMessage(message);
        setName(name);
        setDescription(description);
    }
    public ParameterDocument(){
        setId(EMPTY);
        setMessage(EMPTY);
        setName(EMPTY);
        setDescription(EMPTY);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setMessage(String message) {
        this.message = trim(message);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public void setDescription(String description) {
        this.description = trim(description);
    }
    public static ParameterDocument build (){return new ParameterDocument();}
}
package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import co.edu.uco.utils.helper.UtilText;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;


import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.COLLECTION_ENVIRONMENT_TYPE;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
@Document(collection = COLLECTION_ENVIRONMENT_TYPE)
public final class EnvironmentTypeDocument {
    @Id
    private String id;
    private String name;
    public EnvironmentTypeDocument() {
        setId(EMPTY);
        setName(UtilText.EMPTY);
    }
    public EnvironmentTypeDocument(String id, String name) {
        setId(id);
        setName(name);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static EnvironmentTypeDocument build() {
        return new EnvironmentTypeDocument();
    }
}
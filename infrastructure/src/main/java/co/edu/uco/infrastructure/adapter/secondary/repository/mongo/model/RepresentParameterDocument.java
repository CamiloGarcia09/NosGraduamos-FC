package co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model;

import co.edu.uco.core.domain.data.ApplicationData;
import co.edu.uco.utils.helper.UtilObject;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.COLLECTION_REPRESENT_PARAMETER;
import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
@Document(collection = COLLECTION_REPRESENT_PARAMETER)
public final class RepresentParameterDocument {
    @Id
    private String id;
    private String start;
    private String end;
    @DBRef
    private ApplicationData application;
    private boolean defaultParameter;
    private boolean parameter;
    public RepresentParameterDocument(String id, String start, String end, ApplicationData application,
                                      boolean defaultParameter, boolean parameter) {
        setId(id);
        setStart(start);
        setEnd(end);
        setApplication(application);
        setDefaultParameter(defaultParameter);
        setParameter(parameter);
    }
    public RepresentParameterDocument() {
        setId(EMPTY);
        setStart(EMPTY);
        setEnd(EMPTY);
        setApplication(ApplicationData.build());
        setDefaultParameter(true);
        setParameter(true);
    }
    public void setId(String id) {
        this.id = trim(id);
    }
    public void setStart(String start) {
        this.start = trim(start);
    }
    public void setEnd(String end) {
        this.end = trim(end);
    }
    public void setApplication(ApplicationData application) { this.application = getDefaultIsNullObject(application, ApplicationData.build());}
    public void setDefaultParameter(boolean defaultParameter) {this.defaultParameter = UtilObject.getDefaultIsNullObject(defaultParameter, true);}
    public void setParameter(boolean parameter) {
        this.parameter = UtilObject.getDefaultIsNullObject(parameter, true);
    }
    public static RepresentParameterDocument build(){return new RepresentParameterDocument();}
}
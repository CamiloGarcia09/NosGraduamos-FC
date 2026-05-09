package co.edu.uco.core.domain.data;

import co.edu.uco.utils.helper.UtilObject;
import co.edu.uco.utils.helper.UtilText;
import co.edu.uco.utils.helper.UtilUUID;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class RepresentParameterData {
    private UUID id;
    private String start;
    private String end;
    private ApplicationData application;
    private boolean defaultParameter;
    private boolean parameter;

    public RepresentParameterData(UUID id, String start, String end, ApplicationData application, boolean defaultParameter,
                                  boolean parameter) {
        setId(id);
        setStart(start);
        setEnd(end);
        setApplication(application);
        setDefaultParameter(defaultParameter);
        setParameter(parameter);
    }

    public RepresentParameterData() {
        setId(UtilUUID.getNewUUID());
        setStart(UtilText.EMPTY);
        setEnd(UtilText.EMPTY);
        setApplication(ApplicationData.build());
        setDefaultParameter(true);
        setParameter(true);
    }

    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setStart(String start) {
        this.start = trim(start);
    }
    public void setEnd(String end) {
        this.end = trim(end);
    }
    public void setApplication(ApplicationData application) {
        this.application = getDefaultIsNullObject(application, ApplicationData.build());
    }
    public void setDefaultParameter(boolean defaultParameter) {
        this.defaultParameter = UtilObject.getDefaultIsNullObject(defaultParameter, true);
    }
    public void setParameter(boolean parameter) {
        this.parameter = UtilObject.getDefaultIsNullObject(parameter, true);
    }
}
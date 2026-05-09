package co.edu.uco.core.domain.data;

import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;
import static co.edu.uco.utils.helper.UtilUUID.getNewUUID;

@Getter
public final class EnvironmentData {
    private UUID id;
    private String name;
    private ApplicationData application;
    public EnvironmentData(UUID id, String name, ApplicationData application) {
        setId(id);
        setName(name);
        setApplication(application);
    }
    public EnvironmentData() {
        setId(getNewUUID());
        setName(EMPTY);
        setApplication(ApplicationData.build());
    }
    public void setId(UUID id) { this.id = getDefaultUUID(id);}
    public void setName(String name) {
        this.name = trim(name);
    }
    public void setApplication(ApplicationData application) { this.application = getDefaultIsNullObject(application, ApplicationData.build());}
    public static EnvironmentData build() {
        return new EnvironmentData();
    }
}
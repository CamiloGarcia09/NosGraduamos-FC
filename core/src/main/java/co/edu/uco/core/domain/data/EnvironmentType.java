package co.edu.uco.core.domain.data;

import co.edu.uco.utils.helper.UtilText;
import co.edu.uco.utils.helper.UtilUUID;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class EnvironmentType {
    private UUID id;
    private String name;
    public EnvironmentType() {
        setId(UtilUUID.getNewUUID());
        setName(UtilText.EMPTY);
    }
    public EnvironmentType(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static EnvironmentType build() {
        return new EnvironmentType();
    }
}
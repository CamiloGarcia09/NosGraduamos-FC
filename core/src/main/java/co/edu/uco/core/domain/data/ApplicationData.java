package co.edu.uco.core.domain.data;

import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilUUID.getNewUUID;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class ApplicationData {
    private UUID id;
    private String name;
    public ApplicationData() {
        setId(getNewUUID());
        setName(EMPTY);
    }
    public ApplicationData(UUID id, String name) {
        setId(id);
        setName(name);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static ApplicationData build() {
        return new ApplicationData();
    }
    public static ApplicationData build(UUID id, String name) {
        return new ApplicationData(id, name);
    }
}
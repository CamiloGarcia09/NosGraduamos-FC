package co.edu.uco.core.domain.data;

import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;
import static co.edu.uco.utils.helper.UtilUUID.getNewUUID;

@Getter
public final class StatusTokenData {
    private UUID id;
    private String name;
    public StatusTokenData() {
        setId(getNewUUID());
        setName(EMPTY);
    }
    public StatusTokenData(UUID id, String name) {
        setId(id);
        setName(name);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static StatusTokenData build() {
        return new StatusTokenData();
    }
}
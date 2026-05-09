package co.edu.uco.core.domain.data;

import co.edu.uco.utils.helper.UtilText;
import co.edu.uco.utils.helper.UtilUUID;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class MessageEnvironmentStateData {
    private UUID id;
    private String name;
    public MessageEnvironmentStateData(UUID id, String name) {
        setId(id);
        setName(name);
    }
    public MessageEnvironmentStateData() {
        setId(UtilUUID.getNewUUID());
        setName(UtilText.EMPTY);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String id) {
        this.name = trim(id);
    }
    public static MessageEnvironmentStateData build() {
        return new MessageEnvironmentStateData();
    }
}
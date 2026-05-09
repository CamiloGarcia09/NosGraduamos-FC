package co.edu.uco.core.domain.data;
import co.edu.uco.utils.helper.UtilUUID;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilUUID.getNewUUID;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;
import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
public final class MessageCategoryData {
    private UUID id;
    private String name;

    public MessageCategoryData() {
        setId(getNewUUID());
        setName(EMPTY);
    }
    public MessageCategoryData(UUID id, String name) {
        setId(id);
        setName(name);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public static MessageCategoryData build() {
        return new MessageCategoryData();
    }
    public static MessageCategoryData build(String name) {
        return new MessageCategoryData(UtilUUID.getNewUUID(), name);
    }
}
package co.edu.uco.core.domain.domains;

import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class MessageCategoryDomain {
    private UUID id;
    private String name;

    public MessageCategoryDomain(UUID id, String name) {
        setId(id);
        setName(name);
    }

    public static MessageCategoryDomain create(UUID id, String name) {
        return new MessageCategoryDomain(id, name);
    }

    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setName(String name) {
        this.name = trim(name);
    }
}
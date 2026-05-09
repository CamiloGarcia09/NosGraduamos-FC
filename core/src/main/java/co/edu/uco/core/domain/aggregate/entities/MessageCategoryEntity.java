package co.edu.uco.core.domain.aggregate.entities;

import co.edu.uco.core.domain.aggregate.Entity;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class MessageCategoryEntity extends Entity<UUID> {
    private UUID id;
    private String name;
    @Override
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
}
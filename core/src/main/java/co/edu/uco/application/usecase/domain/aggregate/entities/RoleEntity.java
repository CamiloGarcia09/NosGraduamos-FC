package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class RoleEntity extends Entity<UUID> {

    private UUID id;
    private String name;

    @Override
    public void setId(final UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setName(final String name) {
        this.name = trim(name);
    }
}

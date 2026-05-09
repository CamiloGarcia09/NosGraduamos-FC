package co.edu.uco.core.domain.aggregate.entities;

import co.edu.uco.core.domain.aggregate.Entity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.utils.helper.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class FunctionalityEntity extends Entity<UUID> {
    private UUID id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Override
    public void setId(UUID id) {this.id = getDefaultUUID(id);}
    public void setName(String name) {
        this.name = trim(name);
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = getDefaultTimeIfNull(startDate);
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = getDefaultTimeIfNull(endDate);
    }
}
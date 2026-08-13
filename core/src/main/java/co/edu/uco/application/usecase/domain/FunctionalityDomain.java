package co.edu.uco.application.usecase.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilDate.getDefaultTimeIfNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class FunctionalityDomain {
    private UUID id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public FunctionalityDomain(UUID id, String name, LocalDateTime startDate, LocalDateTime endDate) {
        setId(id);
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public static FunctionalityDomain create(UUID id, String name, LocalDateTime startDate, LocalDateTime endDate) {
        return new FunctionalityDomain(id, name, startDate, endDate);
    }

    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }

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
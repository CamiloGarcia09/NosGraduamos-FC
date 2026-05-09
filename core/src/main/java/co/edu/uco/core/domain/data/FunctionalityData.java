package co.edu.uco.core.domain.data;


import co.edu.uco.utils.helper.UtilDate;
import co.edu.uco.utils.helper.UtilObject;
import co.edu.uco.utils.helper.UtilUUID;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.utils.helper.UtilDate.TIME;
import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilText.trim;
import static co.edu.uco.utils.helper.UtilUUID.getDefaultUUID;

@Getter
public final class FunctionalityData {
    private UUID id;
    private String name;
    private ApplicationData application;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    public FunctionalityData() {
        setId(UUID.randomUUID());
        setName(EMPTY);
        setStartDate(TIME);
        setEndDate(TIME);
    }
    public FunctionalityData(UUID id, String name, ApplicationData application, LocalDateTime startDate, LocalDateTime endDate) {
        setId(id);
        setName(name);
        setApplication(application);
        setStartDate(startDate);
        setEndDate(endDate);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setName(String name) {
        this.name = trim(name);
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = UtilDate.getDefaultTimeIfNull(startDate);
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = UtilDate.getDefaultTimeIfNull(endDate);
    }
    public void setApplication(ApplicationData application) {this.application = UtilObject.getDefaultIsNullObject(application, ApplicationData.build());}
    public static FunctionalityData build() {
        return new FunctionalityData();
    }
    public static FunctionalityData build(String name) { return new FunctionalityData(UtilUUID.getNewUUID(), name, ApplicationData.build(), TIME, TIME);}
}
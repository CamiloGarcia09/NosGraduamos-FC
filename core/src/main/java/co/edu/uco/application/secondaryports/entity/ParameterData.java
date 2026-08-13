package co.edu.uco.application.secondaryports.entity;

import co.edu.uco.crosscutting.helpers.UtilObject;
import co.edu.uco.crosscutting.helpers.UtilText;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class ParameterData {
    private UUID id;
    private MessageData message;
    private String name;
    private String description;
    public ParameterData(UUID id, MessageData message, String name, String description){
        setId(id);
        setMessage(message);
        setName(name);
        setDescription(description);
    }
    public ParameterData(){
        setId(UtilUUID.getNewUUID());
        setName(UtilText.EMPTY);
        setDescription(UtilText.EMPTY);
    }
    public void setId(UUID id) {
        this.id = getDefaultUUID(id);
    }
    public void setMessage(MessageData message) { this.message = UtilObject.getDefaultIsNullObject(message, MessageData.build());}
    public void setName(String name) {
        this.name = trim(name);
    }
    public void setDescription(String description) {
        this.description = trim(description);
    }
}
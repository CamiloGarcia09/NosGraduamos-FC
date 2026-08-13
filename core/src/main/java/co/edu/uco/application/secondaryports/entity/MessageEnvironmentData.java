package co.edu.uco.application.secondaryports.entity;

import co.edu.uco.crosscutting.helpers.UtilObject;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class MessageEnvironmentData {
    private UUID id;
    private MessageData message;
    private EnvironmentType environmentType;
    private MessageEnvironmentStateData stateData;

    public MessageEnvironmentData(UUID id, MessageData message, EnvironmentType environmentType) {
        setId(id);
        setMessage(message);
        setEnvironmentType(environmentType);
    }
    public MessageEnvironmentData() {
        setId(UtilUUID.getNewUUID());
        setMessage(MessageData.build());
        setEnvironmentType(EnvironmentType.build());
    }
    public void setId(UUID id) {
        this.id = UtilUUID.getDefaultUUID(id);
    }
    public void setMessage(MessageData message) {
        this.message = UtilObject.getDefaultIsNullObject(message, MessageData.build());
    }
    public void setEnvironmentType(EnvironmentType environmentType) {
        this.environmentType = UtilObject.getDefaultIsNullObject(environmentType, EnvironmentType.build());
    }
    public void setStateData(MessageEnvironmentStateData stateData) {
        this.stateData = UtilObject.getDefaultIsNullObject(stateData, MessageEnvironmentStateData.build());
    }
}
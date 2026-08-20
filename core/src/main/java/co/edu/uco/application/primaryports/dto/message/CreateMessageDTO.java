package co.edu.uco.application.primaryports.dto.message;

import lombok.Builder;
import lombok.Getter;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Getter
@Builder
public final class CreateMessageDTO {

    private String code;
    private String title;
    private String content;
    private String typeId;
    private String categoryId;
    private String statusId;
    private String applicationId;
    private String application;
    private String functionalityId;
    private String environmentId;
    private String messageEnvironmentStateId;

    public CreateMessageDTO() {
        setCode(EMPTY);
        setTitle(EMPTY);
        setContent(EMPTY);
        setTypeId(EMPTY);
        setCategoryId(EMPTY);
        setStatusId(EMPTY);
        setApplicationId(EMPTY);
        setApplication(EMPTY);
        setFunctionalityId(EMPTY);
        setEnvironmentId(EMPTY);
        setMessageEnvironmentStateId(EMPTY);
    }

    public CreateMessageDTO(String code, String title, String content, String typeId,
                            String categoryId, String statusId, String applicationId,
                            String application, String functionalityId, String environmentId,
                            String messageEnvironmentStateId) {
        setCode(code);
        setTitle(title);
        setContent(content);
        setTypeId(typeId);
        setCategoryId(categoryId);
        setStatusId(statusId);
        setApplicationId(applicationId);
        setApplication(application);
        setFunctionalityId(functionalityId);
        setEnvironmentId(environmentId);
        setMessageEnvironmentStateId(messageEnvironmentStateId);
    }

    public void setCode(String code) {
        this.code = trim(code);
    }

    public void setTitle(String title) {
        this.title = trim(title);
    }

    public void setContent(String content) {
        this.content = trim(content);
    }

    public void setTypeId(String typeId) {
        this.typeId = trim(typeId);
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = trim(categoryId);
    }

    public void setStatusId(String statusId) {
        this.statusId = trim(statusId);
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = trim(applicationId);
    }

    public void setApplication(String application) {
        this.application = trim(application);
    }

    public void setFunctionalityId(String functionalityId) {
        this.functionalityId = trim(functionalityId);
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }

    public void setMessageEnvironmentStateId(String messageEnvironmentStateId) {
        this.messageEnvironmentStateId = trim(messageEnvironmentStateId);
    }
}

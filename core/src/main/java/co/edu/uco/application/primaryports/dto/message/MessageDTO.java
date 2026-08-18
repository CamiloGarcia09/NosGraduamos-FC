package co.edu.uco.application.primaryports.dto.message;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;

public record MessageDTO(String code, String title, String content, String type, String category, String application,
                         String functionality) {

    public MessageDTO(String code, String title, String content, String type, String category, String application,
                      String functionality) {
        this.code = trim(code);
        this.title = trim(title);
        this.content = trim(content);
        this.application = trim(application);
        this.type = trim(type);
        this.category = trim(category);
        this.functionality = trim(functionality);
    }

    public static MessageDTO create(String code, String title, String content, String type, String category,
                                    String application, String functionality) {
        return new MessageDTO(code, title, content, type, category, application, functionality);
    }
}
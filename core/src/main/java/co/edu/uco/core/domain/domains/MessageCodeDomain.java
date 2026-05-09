package co.edu.uco.core.domain.domains;

import lombok.Getter;

import static co.edu.uco.utils.helper.UtilText.trim;

@Getter
public final class MessageCodeDomain {
    private String code;
    public MessageCodeDomain(String code) {
        setCode(code);
    }
    public MessageCodeDomain() {

    }
    public static MessageCodeDomain create(String code) {
        return new MessageCodeDomain(code);
    }

    public void setCode(String code) {
        this.code = trim(code);
    }
}
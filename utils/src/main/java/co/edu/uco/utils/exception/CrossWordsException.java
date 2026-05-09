package co.edu.uco.utils.exception;

import co.edu.uco.utils.exception.enumeration.ExceptionLocation;
import co.edu.uco.utils.exception.enumeration.ExceptionType;

import java.io.Serial;

import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;
import static co.edu.uco.utils.helper.UtilText.trim;

public class CrossWordsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3393801537376736983L;
    private String userMessage;
    private String technicalMessage;
    private Exception rootException;
    private ExceptionType type;
    private ExceptionLocation location;

    public CrossWordsException(String userMessage, String technicalMessage, Exception rootException) {
        super();
        setUserMessage(userMessage);
        setTechnicalMessage(technicalMessage);
        setRootException(rootException);
        setType(ExceptionType.GENERAL);
        setLocation(ExceptionLocation.GENERAL);
    }

    public static CrossWordsException build(String technicalMessage) {
        return new CrossWordsException(null, technicalMessage, null, ExceptionType.TECHNICAL, ExceptionLocation.GENERAL);
    }

    public static CrossWordsException build(String technicalMessage, Exception rootException) {
        return new CrossWordsException(null, technicalMessage, rootException, ExceptionType.TECHNICAL, ExceptionLocation.GENERAL);
    }

    public static CrossWordsException build(String technicalMessage, String userMessage, Exception rootException, ExceptionType type, ExceptionLocation location) {
        return new CrossWordsException(userMessage, technicalMessage, rootException, type, location);
    }

    public static CrossWordsException buildInfrastructure(String technicalMessage, String userMessage, Exception rootException, ExceptionType type) {
        return new CrossWordsException(userMessage, technicalMessage, rootException, type, ExceptionLocation.INFRASTRUCTURE);
    }
    public static CrossWordsException buildInfrastructure(String technicalMessage, Exception rootException) {
        return new CrossWordsException(null, technicalMessage, rootException, ExceptionType.TECHNICAL, ExceptionLocation.INFRASTRUCTURE);
    }
    public static CrossWordsException buildInfrastructure(String technicalMessage, String userMessage, ExceptionType type) {
        return new CrossWordsException(userMessage, technicalMessage, null, type, ExceptionLocation.INFRASTRUCTURE);
    }

    protected CrossWordsException(String userMessage, String technicalMessage, Exception rootException, ExceptionType type, ExceptionLocation location) {
        super();
        setUserMessage(userMessage);
        setTechnicalMessage(technicalMessage);
        setRootException(rootException);
        setLocation(location);
        setType(type);
    }

    public String getUserMessage() {
        return userMessage;
    }
    public String getTechnicalMessage() {
        return technicalMessage;
    }
    public Exception getRootException() {
        return rootException;
    }
    public ExceptionLocation getLocation() {
        return location;
    }
    public ExceptionType getType() { return type; }

    private void setUserMessage(String userMessage) {
        this.userMessage = trim(userMessage);
    }
    private void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = (technicalMessage);
    }
    private void setRootException(Exception rootException) { this.rootException = getDefaultIsNullObject(rootException, new Exception());}
    public void setType(ExceptionType type) {
        this.type = getDefaultIsNullObject(type, ExceptionType.GENERAL);
    }
    public void setLocation(ExceptionLocation location) { this.location = getDefaultIsNullObject(location, ExceptionLocation.GENERAL);}
}

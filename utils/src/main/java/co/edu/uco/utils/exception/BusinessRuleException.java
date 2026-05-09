package co.edu.uco.utils.exception;

import co.edu.uco.utils.exception.enumeration.ExceptionLocation;
import co.edu.uco.utils.exception.enumeration.ExceptionType;

import static co.edu.uco.utils.helper.UtilObject.getDefaultIsNullObject;

public class BusinessRuleException extends BusinessException {
    private ExceptionType type;
    private ExceptionLocation location;
    protected BusinessRuleException(String userMessage, String technicalMessage, Exception rootException, ExceptionType type, ExceptionLocation location) {
        super(userMessage, technicalMessage, rootException);
        setLocation(location);
        setType(type);
    }

    public BusinessRuleException(String userMessage, String technicalMessage) {
        super(userMessage, technicalMessage, null, ExceptionType.BUSINESS_RULE,ExceptionLocation.APPLICATION);
    }

    public BusinessRuleException(String userMessage, String technicalMessage, Exception rootException, ExceptionType type, ExceptionLocation location, ExceptionType type1, ExceptionLocation location1) {
        super(userMessage, technicalMessage, rootException, type, location);
        this.type = type1;
        this.location = location1;
    }

    public static BusinessRuleException buildUserException(String userMessage) {
        return new BusinessRuleException(userMessage, userMessage, null, ExceptionType.BUSINESS_RULE, null);
    }

    public static BusinessRuleException buildTechnicalException(String technicalMessage) {
        return new BusinessRuleException(null, technicalMessage, null, ExceptionType.TECHNICAL, null);
    }

    public static BusinessRuleException buildTechnicalException(String userMessage, String technicalMessage) {
        return new BusinessRuleException(userMessage, technicalMessage, null, ExceptionType.TECHNICAL, ExceptionLocation.APPLICATION);
    }

    public static BusinessRuleException buildTechnicalException(String technicalMessage, Exception rootException, ExceptionLocation location) {
        return new BusinessRuleException(null, technicalMessage, rootException, ExceptionType.TECHNICAL, location);
    }

    public ExceptionType getType() {
        return type;
    }

    public void setType(ExceptionType type) {
        this.type = getDefaultIsNullObject(type, ExceptionType.GENERAL);
    }

    public ExceptionLocation getLocation() {
        return location;
    }

    public void setLocation(ExceptionLocation location) {
        this.location = getDefaultIsNullObject(location, ExceptionLocation.GENERAL);
    }
}

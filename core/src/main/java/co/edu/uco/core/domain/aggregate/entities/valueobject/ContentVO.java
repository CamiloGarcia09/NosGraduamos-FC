package co.edu.uco.core.domain.aggregate.entities.valueobject;

import co.edu.uco.core.domain.customexception.ContentCanNotBeEmptyException;
import co.edu.uco.core.domain.customexception.SizeContentLessThanTenException;
import co.edu.uco.core.domain.customexception.SizeContentMoreThanOneHundred;
import co.edu.uco.utils.helper.UtilNumeric;
import lombok.Getter;

import static co.edu.uco.utils.helper.UtilText.isEmptyOrNull;

@Getter
public final class ContentVO {
    private String content;
    public ContentVO(String content) {
        setContent(content);
    }
    public void setContent(String content) {
        validateContent(content);
        validateSizeLessThanTen(content);
        validateSizeMoreThanOneHundred(content);
        this.content = content;
    }
    private void validateContent(String content) {
        if (isEmptyOrNull(content)) {
            ContentCanNotBeEmptyException.report();
        }
    }
    private void validateSizeMoreThanOneHundred(String content) {
        if (UtilNumeric.isGreaterThan(content.length(), 100)) {
            SizeContentMoreThanOneHundred.report();
        }
    }
    private void validateSizeLessThanTen(String content) {
        if (UtilNumeric.isLessThan(content.length(), 10)) {
            SizeContentLessThanTenException.report();
        }
    }
}
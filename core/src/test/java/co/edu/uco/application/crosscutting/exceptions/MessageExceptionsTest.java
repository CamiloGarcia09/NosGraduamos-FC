package co.edu.uco.application.crosscutting.exceptions;

import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageExceptionsTest {

    @Test
    void titleCanNotBeEmpty_report_throwsBusinessRuleException() {
        assertThatThrownBy(TitleCanNotBeEmptyException::report)
                .isInstanceOf(TitleCanNotBeEmptyException.class)
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void sizeTitleLessThanTen_report_throwsBusinessRuleException() {
        assertThatThrownBy(SizeTitleLessThanTenException::report)
                .isInstanceOf(SizeTitleLessThanTenException.class)
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void sizeTitleMoreThanFifty_report_throwsBusinessRuleException() {
        assertThatThrownBy(SizeTitleMoreThanFiftyException::report)
                .isInstanceOf(SizeTitleMoreThanFiftyException.class)
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void contentCanNotBeEmpty_report_throwsBusinessRuleException() {
        assertThatThrownBy(ContentCanNotBeEmptyException::report)
                .isInstanceOf(ContentCanNotBeEmptyException.class)
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void sizeContentLessThanTen_report_throwsBusinessRuleException() {
        assertThatThrownBy(SizeContentLessThanTenException::report)
                .isInstanceOf(SizeContentLessThanTenException.class)
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void sizeContentMoreThanOneHundred_report_throwsBusinessRuleException() {
        assertThatThrownBy(SizeContentMoreThanOneHundred::report)
                .isInstanceOf(SizeContentMoreThanOneHundred.class)
                .isInstanceOf(BusinessRuleException.class);
    }
}
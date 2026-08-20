package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageRequestTypeValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private PageRequestTypeValidator validator;

    @Test
    void validate_returnsWithoutError_whenDataIsNull() {
        assertThatCode(() -> validator.validate(null)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_forValidPageRequest() {
        PageRequestDTO dto = PageRequestDTO.builder()
                .page("1")
                .size("20")
                .columnSort("id")
                .sort("ASC")
                .build();
        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenPageFieldsAreNullOrEmpty() {
        PageRequestDTO dto = PageRequestDTO.builder()
                .page("")
                .size(null)
                .columnSort("")
                .sort(null)
                .build();
        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsBusinessRuleException_whenPageIsNotNumeric() {
        when(catalogPort.getMessage("FUN_033")).thenReturn("Attribute %s must contain only numbers");
        PageRequestDTO dto = PageRequestDTO.builder().page("abc").size("20").columnSort("id").sort("ASC").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Attribute page must contain only numbers"));
    }

    @Test
    void validate_throwsBusinessRuleException_whenSizeIsNotNumeric() {
        when(catalogPort.getMessage("FUN_033")).thenReturn("Attribute %s must contain only numbers");
        PageRequestDTO dto = PageRequestDTO.builder().page("1").size("abc").columnSort("id").sort("ASC").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Attribute size must contain only numbers"));
    }

    @Test
    void validate_throwsBusinessRuleException_whenColumnSortIsNotLetters() {
        when(catalogPort.getMessage("FUN_043")).thenReturn("Attribute %s must contain only letters");
        PageRequestDTO dto = PageRequestDTO.builder().page("1").size("20").columnSort("id123").sort("ASC").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Attribute columnSort must contain only letters"));
    }

    @Test
    void validate_throwsBusinessRuleException_whenSortIsNotLetters() {
        when(catalogPort.getMessage("FUN_043")).thenReturn("Attribute %s must contain only letters");
        PageRequestDTO dto = PageRequestDTO.builder().page("1").size("20").columnSort("id").sort("ASC1").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("Attribute sort must contain only letters"));
    }
}
package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageRequestTypeValidatorTest {

    @Mock
    private CatalogPort catalogPort;

    private PageRequestTypeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PageRequestTypeValidator(catalogPort);
    }

    @Test
    void validate_acceptsNullData() {
        assertDoesNotThrow(() -> validator.validate(null));
    }

    @Test
    void validate_acceptsNumericPageAndSizeAndAlphabeticSort() {
        PageRequestDTO dto = PageRequestDTO.builder()
                .page("1")
                .size("50")
                .columnSort("id")
                .sort("ASC")
                .build();

        assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    void validate_acceptsEmptyFields() {
        PageRequestDTO dto = PageRequestDTO.builder()
                .page("")
                .size("")
                .columnSort("")
                .sort("")
                .build();

        assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    void validate_throwsBusinessRule_whenPageIsNotNumeric() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_033.getCode())).thenReturn("El atributo %s debe ser numerico");
        PageRequestDTO dto = PageRequestDTO.builder().page("abc").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El atributo page debe ser numerico"));
    }

    @Test
    void validate_throwsBusinessRule_whenSizeIsNotNumeric() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_033.getCode())).thenReturn("El atributo %s debe ser numerico");
        PageRequestDTO dto = PageRequestDTO.builder().size("abc").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El atributo size debe ser numerico"));
    }

    @Test
    void validate_throwsBusinessRule_whenColumnSortIsNotAlphabetic() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_043.getCode())).thenReturn("El atributo %s debe ser alfabetico");
        PageRequestDTO dto = PageRequestDTO.builder().columnSort("123").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El atributo columnSort debe ser alfabetico"));
    }

    @Test
    void validate_throwsBusinessRule_whenSortIsNotAlphabetic() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_043.getCode())).thenReturn("El atributo %s debe ser alfabetico");
        PageRequestDTO dto = PageRequestDTO.builder().sort("123").build();

        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(ex -> assertThat(((BusinessRuleException) ex).getUserMessage())
                        .isEqualTo("El atributo sort debe ser alfabetico"));
    }
}
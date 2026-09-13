package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PageRequestDTOValidatorTest {

    @Mock
    private CompositeValidator<PageRequestDTO> compositeValidator;

    @Test
    void validate_delegatesToCompositeValidator() {
        PageRequestDTOValidator validator = new PageRequestDTOValidator(compositeValidator);
        PageRequestDTO dto = PageRequestDTO.builder().page("1").build();

        validator.validate(dto);

        verify(compositeValidator).validate(dto);
    }
}
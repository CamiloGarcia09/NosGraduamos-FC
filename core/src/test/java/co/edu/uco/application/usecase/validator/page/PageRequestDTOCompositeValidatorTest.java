package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageRequestDTOCompositeValidatorTest {

    @Mock
    private Validator<PageRequestDTO> validator;

    @Mock
    private CatalogPort catalogPort;

    @Test
    void validate_throwsCrossWordsException_whenNoValidators() {
        when(catalogPort.getMessage("FUN_034")).thenReturn("no validators");
        PageRequestDTOCompositeValidator composite =
                new PageRequestDTOCompositeValidator(List.of(), catalogPort);

        assertThatThrownBy(() -> composite.validate(PageRequestDTO.builder().build()))
                .isInstanceOf(co.edu.uco.crosscutting.exceptions.CrossWordsException.class)
                .satisfies(ex -> assertThat(((co.edu.uco.crosscutting.exceptions.CrossWordsException) ex)
                        .getTechnicalMessage()).isEqualTo("no validators"));
    }

    @Test
    void validate_throwsCrossWordsException_whenDataIsNull() {
        when(catalogPort.getMessage("FUN_010")).thenReturn("data is null");
        PageRequestDTOCompositeValidator composite =
                new PageRequestDTOCompositeValidator(List.of(validator), catalogPort);

        assertThatThrownBy(() -> composite.validate(null))
                .isInstanceOf(co.edu.uco.crosscutting.exceptions.CrossWordsException.class)
                .satisfies(ex -> assertThat(((co.edu.uco.crosscutting.exceptions.CrossWordsException) ex)
                        .getTechnicalMessage()).isEqualTo("data is null"));
    }
}
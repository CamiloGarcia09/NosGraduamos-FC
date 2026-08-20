package co.edu.uco.application.usecase.validator.page;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimplePageRequestCompositeValidatorTest {

    @Mock
    private Validator<co.edu.uco.application.secondaryports.repository.SimplePageRequest> validator;

    @Mock
    private CatalogPort catalogPort;

    @Test
    void validate_delegatesToParentCompositeLogic() {
        when(catalogPort.getMessage("FUN_034")).thenReturn("empty validators");

        SimplePageRequestCompositeValidator composite =
                new SimplePageRequestCompositeValidator(List.of(), catalogPort);

        assertThatThrownBy(() -> composite.validate(new co.edu.uco.application.secondaryports.repository.SimplePageRequest()))
                .isInstanceOf(co.edu.uco.crosscutting.exceptions.CrossWordsException.class)
                .satisfies(ex -> assertThat(((co.edu.uco.crosscutting.exceptions.CrossWordsException) ex)
                        .getTechnicalMessage()).isEqualTo("empty validators"));
    }

    @Test
    void validate_throwsForNullData() {
        when(catalogPort.getMessage("FUN_010")).thenReturn("null data");
        SimplePageRequestCompositeValidator composite =
                new SimplePageRequestCompositeValidator(List.of(validator), catalogPort);

        assertThatThrownBy(() -> composite.validate(null))
                .isInstanceOf(co.edu.uco.crosscutting.exceptions.CrossWordsException.class)
                .satisfies(ex -> assertThat(((co.edu.uco.crosscutting.exceptions.CrossWordsException) ex)
                        .getTechnicalMessage()).isEqualTo("null data"));
    }
}
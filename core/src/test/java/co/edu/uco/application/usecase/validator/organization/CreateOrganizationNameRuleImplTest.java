package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationNameRuleImplTest {

    private static final String REQUIRED_MESSAGE = "El nombre de la organización es requerido.";
    private static final String MAX_LENGTH_MESSAGE = "El nombre no puede superar los 50 caracteres.";

    @Mock
    private CatalogPort catalogPort;

    private CreateOrganizationNameRuleImpl rule;

    @BeforeEach
    void setUp() {
        rule = new CreateOrganizationNameRuleImpl(catalogPort);
    }

    @Test
    void validate_acceptsValidName() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("UCO").build();

        assertThatCode(() -> rule.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void validate_acceptsNameAtFiftyCharacterLimit() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("a".repeat(50)).build();

        assertThatCode(() -> rule.validate(dto)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void validate_rejectsMissingName(String name) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_147.getCode())).thenReturn(REQUIRED_MESSAGE);
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name(name).build();

        assertThatThrownBy(() -> rule.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo(REQUIRED_MESSAGE);
        verify(catalogPort).getMessage("FUN_147");
    }

    @Test
    void validate_rejectsNameWithFiftyOneCharacters() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_148.getCode())).thenReturn(MAX_LENGTH_MESSAGE);
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("a".repeat(51)).build();

        assertThatThrownBy(() -> rule.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo(MAX_LENGTH_MESSAGE);
        verify(catalogPort).getMessage("FUN_148");
    }
}

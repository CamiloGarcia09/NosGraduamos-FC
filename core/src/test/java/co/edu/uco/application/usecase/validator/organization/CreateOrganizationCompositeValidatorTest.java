package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationCompositeValidatorTest {

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private CreateOrganizationNameRule nameRule;
    @Mock
    private CreateOrganizationUniqueNameRule uniqueNameRule;

    private CreateOrganizationCompositeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreateOrganizationCompositeValidator(catalogPort, nameRule, uniqueNameRule);
    }

    @Test
    void validate_executesNameAndUniqueRulesInOrder() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("UCO").build();

        validator.validate(dto);

        InOrder orderedRules = inOrder(nameRule, uniqueNameRule);
        orderedRules.verify(nameRule).validate(dto);
        orderedRules.verify(uniqueNameRule).validate(dto);
    }

    @Test
    void validate_rejectsNullDtoWithFun010Message() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())).thenReturn("Datos requeridos");

        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo("Datos requeridos");
        verify(catalogPort).getMessage("FUN_010");
        verifyNoInteractions(nameRule, uniqueNameRule);
    }

    @Test
    void validate_stopsBeforeUniqueRuleWhenNameRuleFails() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("").build();
        BusinessRuleException expected = BusinessRuleException.buildUserException("Nombre requerido");
        doThrow(expected).when(nameRule).validate(dto);

        assertThatThrownBy(() -> validator.validate(dto)).isSameAs(expected);
        verify(uniqueNameRule, never()).validate(dto);
    }
}

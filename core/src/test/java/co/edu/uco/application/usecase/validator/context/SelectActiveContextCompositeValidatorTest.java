package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SelectActiveContextCompositeValidatorTest {

    private final SelectActiveContextIdentifiersRule identifiersRule = mock(SelectActiveContextIdentifiersRule.class);
    private final SelectActiveContextHierarchyRule hierarchyRule = mock(SelectActiveContextHierarchyRule.class);
    private final SelectActiveContextCompositeValidator validator =
            new SelectActiveContextCompositeValidator(identifiersRule, hierarchyRule);
    private final SelectActiveContextDTO context = new SelectActiveContextDTO("org", "app", "env");

    @Test
    void validate_runsIdentifiersBeforeHierarchy() {
        validator.validate(context);

        var order = inOrder(identifiersRule, hierarchyRule);
        order.verify(identifiersRule).validate(context);
        order.verify(hierarchyRule).validate(context);
    }

    @Test
    void validate_shortCircuitsHierarchyWhenIdentifiersFail() {
        BusinessRuleException failure = BusinessRuleException.buildUserException("invalid");
        doThrow(failure).when(identifiersRule).validate(context);

        assertThatThrownBy(() -> validator.validate(context)).isSameAs(failure);
        verify(hierarchyRule, never()).validate(context);
    }
}

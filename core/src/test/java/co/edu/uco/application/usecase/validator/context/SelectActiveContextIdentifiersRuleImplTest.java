package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectActiveContextIdentifiersRuleImplTest {

    private static final String ORGANIZATION_ID = "123e4567-e89b-12d3-a456-426614175001";
    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175002";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614175003";
    private final UUIDValidator uuidValidator = mock(UUIDValidator.class);
    private final CatalogPort catalogPort = mock(CatalogPort.class);
    private final SelectActiveContextIdentifiersRuleImpl rule =
            new SelectActiveContextIdentifiersRuleImpl(uuidValidator, catalogPort);

    @ParameterizedTest
    @MethodSource("invalidContexts")
    void validate_rejectsNullOrMissingIdentifiers(SelectActiveContextDTO context) {
        when(catalogPort.getMessage("FUN_155")).thenReturn("Identifiers required");

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(BusinessRuleException.class)
                .extracting("httpStatus", "userMessage").containsExactly(422, "Identifiers required");
        verify(uuidValidator, never()).validate(any());
    }

    @Test
    void validate_checksEveryIdentifierAsUuidInOrder() {
        SelectActiveContextDTO context = new SelectActiveContextDTO(
                ORGANIZATION_ID, APPLICATION_ID, ENVIRONMENT_ID);

        rule.validate(context);

        var order = inOrder(uuidValidator);
        order.verify(uuidValidator).validate(context.getOrganizationId());
        order.verify(uuidValidator).validate(context.getApplicationId());
        order.verify(uuidValidator).validate(context.getEnvironmentId());
    }

    private static Stream<SelectActiveContextDTO> invalidContexts() {
        return Stream.of(null, new SelectActiveContextDTO(null, APPLICATION_ID, ENVIRONMENT_ID),
                new SelectActiveContextDTO(ORGANIZATION_ID, " ", ENVIRONMENT_ID),
                new SelectActiveContextDTO(ORGANIZATION_ID, APPLICATION_ID, ""));
    }
}

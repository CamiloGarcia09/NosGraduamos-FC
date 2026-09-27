package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextIdentifiersRequiredRuleTest {

    private static final String ORGANIZATION_ID = "123e4567-e89b-12d3-a456-426614175001";
    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175002";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614175003";
    private static final String IDENTIFIERS_REQUIRED_MESSAGE = "Identifiers required";

    @Mock
    private CatalogPort catalogPort;

    private SelectActiveContextIdentifiersRequiredRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextIdentifiersRequiredRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenEveryIdentifierIsPresent() {
        SelectActiveContextDTO context = new SelectActiveContextDTO(
                ORGANIZATION_ID, APPLICATION_ID, ENVIRONMENT_ID);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenIdentifiersAreNotUuids_becauseFormatIsCheckedByOtherRules() {
        SelectActiveContextDTO context = new SelectActiveContextDTO(
                "organization", "application", "environment");

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "[{index}] rejected context")
    @MethodSource("invalidContexts")
    void validate_throwsBusinessRuleUsingFun155_whenAnIdentifierIsMissing(SelectActiveContextDTO context) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_155.getCode()))
                .thenReturn(IDENTIFIERS_REQUIRED_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getHttpStatus,
                                BusinessRuleException::getUserMessage, BusinessRuleException::getType)
                        .containsExactly(422, IDENTIFIERS_REQUIRED_MESSAGE, ExceptionType.BUSINESS_RULE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_155.getCode());
    }

    private static Stream<SelectActiveContextDTO> invalidContexts() {
        return Stream.of(
                null,
                new SelectActiveContextDTO(null, APPLICATION_ID, ENVIRONMENT_ID),
                new SelectActiveContextDTO(ORGANIZATION_ID, "", ENVIRONMENT_ID),
                new SelectActiveContextDTO(ORGANIZATION_ID, APPLICATION_ID, "   "),
                new SelectActiveContextDTO()
        );
    }
}

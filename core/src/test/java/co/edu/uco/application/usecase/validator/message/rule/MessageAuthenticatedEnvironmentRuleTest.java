package co.edu.uco.application.usecase.validator.message.rule;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.message.CreateMessageValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageAuthenticatedEnvironmentRuleTest {

    private static final String AUTHENTICATED_ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614175003";
    private static final String OTHER_ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614175099";
    private static final String NOT_AUTHORIZED_MESSAGE = "Environment not authorized";

    @Mock
    private CatalogPort catalogPort;

    private MessageAuthenticatedEnvironmentRule rule;

    @BeforeEach
    void setUp() {
        rule = new MessageAuthenticatedEnvironmentRule(catalogPort);
    }

    @Test
    void validate_doesNotThrow_whenBodyEnvironmentIdMatchesAuthenticatedEnvironmentId() {
        CreateMessageValidationContext context = context(AUTHENTICATED_ENVIRONMENT_ID,
                AUTHENTICATED_ENVIRONMENT_ID);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void validate_doesNotThrow_whenUuidsUseDifferentCase() {
        CreateMessageValidationContext context = context(AUTHENTICATED_ENVIRONMENT_ID,
                AUTHENTICATED_ENVIRONMENT_ID.toUpperCase());

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "body environmentId=[{0}]")
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void validate_doesNotThrow_whenBodyEnvironmentIdIsAbsent_becauseAuthenticatedEnvironmentIsUsed(
            String bodyEnvironmentId) {
        CreateMessageValidationContext context = context(bodyEnvironmentId, AUTHENTICATED_ENVIRONMENT_ID);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "authenticated environmentId=[{0}]")
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void validate_throwsForbiddenUsingFun145_whenAuthenticatedEnvironmentIdIsMissing(
            String authenticatedEnvironmentId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_145.getCode()))
                .thenReturn(NOT_AUTHORIZED_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(AUTHENTICATED_ENVIRONMENT_ID, authenticatedEnvironmentId)))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, NOT_AUTHORIZED_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_145.getCode());
    }

    @Test
    void validate_throwsForbiddenUsingFun145_whenRequestedEnvironmentDiffersFromAuthenticatedOne() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_145.getCode()))
                .thenReturn(NOT_AUTHORIZED_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(OTHER_ENVIRONMENT_ID, AUTHENTICATED_ENVIRONMENT_ID)))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, NOT_AUTHORIZED_MESSAGE));
    }

    private static CreateMessageValidationContext context(String bodyEnvironmentId,
                                                          String authenticatedEnvironmentId) {
        return new CreateMessageValidationContext(
                CreateMessageDTO.builder().environmentId(bodyEnvironmentId).build(),
                authenticatedEnvironmentId);
    }
}

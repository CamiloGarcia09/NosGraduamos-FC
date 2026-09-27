package co.edu.uco.application.usecase.validator.message.rule;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.message.CreateMessageValidationContext;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageEnvironmentExistsRuleTest {

    private static final UUID APPLICATION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175105");
    private static final UUID ENVIRONMENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175003");
    private static final String DEFAULT_UUID = "00000000-0000-0000-0000-000000000000";
    private static final String NOT_FOUND_MESSAGE = "Environment does not exist";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private MessageEnvironmentExistsRule rule;

    @BeforeEach
    void setUp() {
        rule = new MessageEnvironmentExistsRule(catalogPort, environmentRepository);
    }

    @Test
    void validate_doesNotThrow_whenAuthenticatedEnvironmentExists() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment()));

        assertThatCode(() -> rule.validate(context(ENVIRONMENT_ID.toString()))).doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
    }

    @Test
    void validate_normalizesAuthenticatedEnvironmentIdBeforeTheRepositoryLookup() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment()));

        assertThatCode(() -> rule.validate(context(ENVIRONMENT_ID.toString().toUpperCase())))
                .doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
    }

    @Test
    void validate_throwsForbiddenUsingFun035_whenAuthenticatedEnvironmentDoesNotExist() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString())).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_035.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(ENVIRONMENT_ID.toString())))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, NOT_FOUND_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_035.getCode());
    }

    @Test
    void validate_throwsForbiddenUsingFun035_whenAuthenticatedEnvironmentIdIsMissing() {
        when(environmentRepository.findById(DEFAULT_UUID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_035.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(null)))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, NOT_FOUND_MESSAGE));
        verify(environmentRepository).findById(DEFAULT_UUID);
    }

    private static CreateMessageValidationContext context(String authenticatedEnvironmentId) {
        return new CreateMessageValidationContext(new CreateMessageDTO(), authenticatedEnvironmentId);
    }

    private static EnvironmentData environment() {
        return new EnvironmentData(ENVIRONMENT_ID, "Environment", ApplicationData.build(APPLICATION_ID, "App"));
    }
}

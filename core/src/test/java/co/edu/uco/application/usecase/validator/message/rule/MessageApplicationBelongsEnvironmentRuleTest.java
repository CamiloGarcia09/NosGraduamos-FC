package co.edu.uco.application.usecase.validator.message.rule;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.EnvironmentTypeData;
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
class MessageApplicationBelongsEnvironmentRuleTest {

    private static final UUID APPLICATION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175105");
    private static final UUID OTHER_APPLICATION_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175199");
    private static final UUID ENVIRONMENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175003");
    private static final String OUTSIDE_APPLICATION_MESSAGE = "Environment outside application";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private MessageApplicationBelongsEnvironmentRule rule;

    @BeforeEach
    void setUp() {
        rule = new MessageApplicationBelongsEnvironmentRule(catalogPort, environmentRepository);
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentBelongsToTheRequestedApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(APPLICATION_ID)));

        assertThatCode(() -> rule.validate(context(APPLICATION_ID.toString()))).doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
    }

    @Test
    void validate_doesNotThrow_whenApplicationIdUsesDifferentCase() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(APPLICATION_ID)));

        assertThatCode(() -> rule.validate(context(APPLICATION_ID.toString().toUpperCase())))
                .doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
    }

    @Test
    void validate_throwsForbiddenUsingFun036_whenEnvironmentBelongsToAnotherApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(OTHER_APPLICATION_ID)));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_036.getCode()))
                .thenReturn(OUTSIDE_APPLICATION_MESSAGE);
        CreateMessageValidationContext context = context(APPLICATION_ID.toString());

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, OUTSIDE_APPLICATION_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_036.getCode());
    }

    @Test
    void validate_throwsForbiddenUsingFun036_whenAuthenticatedEnvironmentDoesNotExist() {
        when(environmentRepository.findById(ENVIRONMENT_ID.toString())).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_036.getCode()))
                .thenReturn(OUTSIDE_APPLICATION_MESSAGE);
        CreateMessageValidationContext context = context(APPLICATION_ID.toString());

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getHttpStatus, ForbiddenException::getUserMessage)
                        .containsExactly(403, OUTSIDE_APPLICATION_MESSAGE));
    }

    private static CreateMessageValidationContext context(String applicationId) {
        return new CreateMessageValidationContext(
                CreateMessageDTO.builder().applicationId(applicationId).build(),
                ENVIRONMENT_ID.toString());
    }

    private static EnvironmentData environment(UUID applicationId) {
        return new EnvironmentData(ENVIRONMENT_ID,
                ApplicationData.build(applicationId, "Application"),
                new EnvironmentTypeData(UUID.randomUUID(), "Environment"));
    }
}

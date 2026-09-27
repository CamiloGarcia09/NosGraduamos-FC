package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextEnvironmentBelongsApplicationRuleTest {

    private static final UUID APPLICATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    private static final UUID OTHER_APPLICATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000098");
    private static final String ENVIRONMENT_ID = "30000000-0000-0000-0000-000000000003";
    private static final String CONFLICT_MESSAGE = "Environment hierarchy conflict";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private SelectActiveContextEnvironmentBelongsApplicationRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextEnvironmentBelongsApplicationRule(catalogPort, environmentRepository);
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentBelongsToRequestedApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(
                new EnvironmentData(UUID.fromString(ENVIRONMENT_ID), "Dev",
                        ApplicationData.build(APPLICATION_ID, "App"))));

        assertThatCode(() -> rule.validate(context(APPLICATION_ID.toString())))
                .doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID);
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentDoesNotExist_becauseNothingCanBeConflict() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.empty());

        assertThatCode(() -> rule.validate(context(APPLICATION_ID.toString())))
                .doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID);
    }

    @Test
    void validate_throwsConflictUsingFun160_whenEnvironmentBelongsToAnotherApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(
                new EnvironmentData(UUID.fromString(ENVIRONMENT_ID), "Dev",
                        ApplicationData.build(OTHER_APPLICATION_ID, "Other"))));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_160.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(APPLICATION_ID.toString())))
                .isInstanceOf(ConflictException.class)
                .satisfies(exception -> assertThat((ConflictException) exception)
                        .extracting(ConflictException::getHttpStatus, ConflictException::getUserMessage)
                        .containsExactly(409, CONFLICT_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_160.getCode());
    }

    @ParameterizedTest(name = "applicationId=[{0}]")
    @ValueSource(strings = {"not-a-uuid", "", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsConflictWithoutRepositoryLookup_whenApplicationIdIsNotAUsableUuid(String applicationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_160.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(applicationId)))
                .isInstanceOf(ConflictException.class);

        verifyNoInteractions(environmentRepository);
    }

    @Test
    void validate_throwsConflictWithoutRepositoryLookup_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_160.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(ConflictException.class);

        verifyNoInteractions(environmentRepository);
    }

    @Test
    void validate_throwsConflictWithoutRepositoryLookup_whenEnvironmentIdIsNotAUsableUuid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_160.getCode()))
                .thenReturn(CONFLICT_MESSAGE);
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .applicationId(APPLICATION_ID.toString())
                .environmentId("not-a-uuid")
                .build();

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(ConflictException.class);

        verifyNoInteractions(environmentRepository);
    }

    private static SelectActiveContextDTO context(String applicationId) {
        return SelectActiveContextDTO.builder()
                .applicationId(applicationId)
                .environmentId(ENVIRONMENT_ID)
                .build();
    }
}

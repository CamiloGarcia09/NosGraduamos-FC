package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextEnvironmentExistsRuleTest {

    private static final String ENVIRONMENT_ID = "30000000-0000-0000-0000-000000000003";
    private static final String NOT_FOUND_MESSAGE = "Environment not found";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private EnvironmentRepository environmentRepository;

    private SelectActiveContextEnvironmentExistsRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextEnvironmentExistsRule(catalogPort, environmentRepository);
    }

    @Test
    void validate_doesNotThrow_whenEnvironmentExists() {
        when(environmentRepository.findById(ENVIRONMENT_ID))
                .thenReturn(Optional.of(new EnvironmentData()));
        SelectActiveContextDTO context = context(ENVIRONMENT_ID);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();

        verify(environmentRepository).findById(ENVIRONMENT_ID);
    }

    @Test
    void validate_throwsNotFoundUsingFun158_whenEnvironmentDoesNotExist() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_158.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);
        SelectActiveContextDTO context = context(ENVIRONMENT_ID);

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, NOT_FOUND_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_158.getCode());
    }

    @ParameterizedTest(name = "environmentId=[{0}]")
    @ValueSource(strings = {"not-a-uuid", "", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsNotFoundUsingFun158WithoutRepositoryLookup_whenEnvironmentIdIsNotAUsableUuid(
            String environmentId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_158.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        SelectActiveContextDTO context = context(environmentId);

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(environmentRepository);
    }

    @Test
    void validate_throwsNotFoundWithoutRepositoryLookup_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_158.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(NotFoundException.class);

        verifyNoInteractions(environmentRepository);
    }

    private static SelectActiveContextDTO context(String environmentId) {
        return SelectActiveContextDTO.builder().environmentId(environmentId).build();
    }
}

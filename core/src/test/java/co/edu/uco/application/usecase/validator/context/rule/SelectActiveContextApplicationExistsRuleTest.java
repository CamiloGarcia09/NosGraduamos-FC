package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
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
class SelectActiveContextApplicationExistsRuleTest {

    private static final String APPLICATION_ID = "20000000-0000-0000-0000-000000000002";
    private static final String NOT_FOUND_MESSAGE = "Application not found";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private ApplicationRepository applicationRepository;

    private SelectActiveContextApplicationExistsRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextApplicationExistsRule(catalogPort, applicationRepository);
    }

    @Test
    void validate_doesNotThrow_whenApplicationExists() {
        when(applicationRepository.findById(APPLICATION_ID))
                .thenReturn(Optional.of(ApplicationData.build()));
        SelectActiveContextDTO context = context(APPLICATION_ID);

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();

        verify(applicationRepository).findById(APPLICATION_ID);
    }

    @Test
    void validate_throwsNotFoundUsingFun157_whenApplicationDoesNotExist() {
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_157.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(APPLICATION_ID)))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, NOT_FOUND_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_157.getCode());
    }

    @ParameterizedTest(name = "applicationId=[{0}]")
    @ValueSource(strings = {"not-a-uuid", "", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsNotFoundUsingFun157WithoutRepositoryLookup_whenApplicationIdIsNotAUsableUuid(
            String applicationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_157.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(applicationId)))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(applicationRepository);
    }

    @Test
    void validate_throwsNotFoundWithoutRepositoryLookup_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_157.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(NotFoundException.class);

        verifyNoInteractions(applicationRepository);
    }

    private static SelectActiveContextDTO context(String applicationId) {
        return SelectActiveContextDTO.builder().applicationId(applicationId).build();
    }
}

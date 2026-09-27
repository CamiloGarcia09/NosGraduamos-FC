package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
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
class SelectActiveContextApplicationBelongsOrganizationRuleTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000099");
    private static final String APPLICATION_ID = "20000000-0000-0000-0000-000000000002";
    private static final String CONFLICT_MESSAGE = "Application hierarchy conflict";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private ApplicationRepository applicationRepository;

    private SelectActiveContextApplicationBelongsOrganizationRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextApplicationBelongsOrganizationRule(catalogPort, applicationRepository);
    }

    @Test
    void validate_doesNotThrow_whenApplicationBelongsToRequestedOrganization() {
        when(applicationRepository.findById(APPLICATION_ID))
                .thenReturn(Optional.of(ApplicationData.build(UUID.fromString(APPLICATION_ID), "App", organization(ORGANIZATION_ID))));

        assertThatCode(() -> rule.validate(context(ORGANIZATION_ID.toString())))
                .doesNotThrowAnyException();

        verify(applicationRepository).findById(APPLICATION_ID);
    }

    @Test
    void validate_doesNotThrow_whenApplicationDoesNotExist_becauseNothingCanBeConflict() {
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());

        assertThatCode(() -> rule.validate(context(ORGANIZATION_ID.toString())))
                .doesNotThrowAnyException();

        verify(applicationRepository).findById(APPLICATION_ID);
    }

    @Test
    void validate_throwsConflictUsingFun159_whenApplicationBelongsToAnotherOrganization() {
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(
                Optional.of(ApplicationData.build(UUID.fromString(APPLICATION_ID), "App", organization(OTHER_ORGANIZATION_ID))));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(ORGANIZATION_ID.toString())))
                .isInstanceOf(ConflictException.class)
                .satisfies(exception -> assertThat((ConflictException) exception)
                        .extracting(ConflictException::getHttpStatus, ConflictException::getUserMessage)
                        .containsExactly(409, CONFLICT_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_159.getCode());
    }

    @Test
    void validate_throwsConflictUsingFun159_whenApplicationHasNoOwnerOrganization() {
        ApplicationData application = ApplicationData.build(UUID.fromString(APPLICATION_ID), "App");
        when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(ORGANIZATION_ID.toString())))
                .isInstanceOf(ConflictException.class);

        verify(applicationRepository).findById(APPLICATION_ID);
    }

    @ParameterizedTest(name = "organizationId=[{0}]")
    @ValueSource(strings = {"not-a-uuid", "", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsConflictWithoutRepositoryLookup_whenOrganizationIdIsNotAUsableUuid(String organizationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(context(organizationId)))
                .isInstanceOf(ConflictException.class);

        verifyNoInteractions(applicationRepository);
    }

    @Test
    void validate_throwsConflictWithoutRepositoryLookup_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn(CONFLICT_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(ConflictException.class);

        verifyNoInteractions(applicationRepository);
    }

    @Test
    void validate_throwsConflictWithoutRepositoryLookup_whenApplicationIdIsNotAUsableUuid() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn(CONFLICT_MESSAGE);
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .organizationId(ORGANIZATION_ID.toString())
                .applicationId("not-a-uuid")
                .build();

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(ConflictException.class);

        verifyNoInteractions(applicationRepository);
    }

    private static SelectActiveContextDTO context(String organizationId) {
        return SelectActiveContextDTO.builder()
                .organizationId(organizationId)
                .applicationId(APPLICATION_ID)
                .build();
    }

    private static OrganizationEntity organization(UUID id) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(id);
        organization.setName("Organization");
        return organization;
    }
}

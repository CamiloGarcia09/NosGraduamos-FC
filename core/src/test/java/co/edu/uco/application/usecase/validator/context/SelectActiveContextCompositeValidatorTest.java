package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.ConflictException;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextCompositeValidatorTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID APPLICATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    private static final UUID ENVIRONMENT_ID = UUID.fromString("30000000-0000-0000-0000-000000000003");
    private static final UUID OTHER_ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000099");
    private static final UUID OTHER_APPLICATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000098");

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private EnvironmentRepository environmentRepository;

    private SelectActiveContextCompositeValidator validator;

    @BeforeEach
    void setUp() {
        lenient().when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_155.getCode()))
                .thenReturn("Identifiers required");
        lenient().when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_038.getCode()))
                .thenReturn("Not a valid UUID");
        validator = new SelectActiveContextCompositeValidator(
                catalogPort, organizationRepository, applicationRepository, environmentRepository);
    }

    @Test
    void validate_doesNotThrow_whenHierarchyIsConsistent() {
        stubExistingHierarchy();

        validator.validate(validContext());
    }

    @Test
    void validate_executesIdentifierThenUuidThenExistenceThenHierarchyChecksInOrder() {
        stubExistingHierarchy();

        validator.validate(validContext());

        InOrder order = inOrder(organizationRepository, applicationRepository, environmentRepository);
        order.verify(organizationRepository).findById(ORGANIZATION_ID);
        order.verify(applicationRepository).findById(APPLICATION_ID.toString());
        order.verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
        order.verify(applicationRepository).findById(APPLICATION_ID.toString());
        order.verify(environmentRepository).findById(ENVIRONMENT_ID.toString());
    }

    @Test
    void validate_shortCircuitsEveryRepository_whenAnIdentifierIsMissing() {
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .organizationId(ORGANIZATION_ID.toString())
                .applicationId(APPLICATION_ID.toString())
                .build();

        assertThatThrownBy(() -> validator.validate(context))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getHttpStatus, BusinessRuleException::getUserMessage)
                        .containsExactly(422, "Identifiers required"));

        verifyNoInteractions(organizationRepository, applicationRepository, environmentRepository);
    }

    @Test
    void validate_shortCircuitsRepositories_whenContextIsNull() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getHttpStatus, BusinessRuleException::getUserMessage)
                        .containsExactly(422, "Identifiers required"));

        verifyNoInteractions(organizationRepository, applicationRepository, environmentRepository);
    }

    @Test
    void validate_shortCircuitsRepositories_whenOrganizationIdIsNotAUuid() {
        SelectActiveContextDTO context = SelectActiveContextDTO.builder()
                .organizationId("not-a-uuid")
                .applicationId(APPLICATION_ID.toString())
                .environmentId(ENVIRONMENT_ID.toString())
                .build();

        assertThatThrownBy(() -> validator.validate(context))
                .isInstanceOf(BusinessRuleException.class)
                .satisfies(exception -> assertThat((BusinessRuleException) exception)
                        .extracting(BusinessRuleException::getUserMessage).isEqualTo("Not a valid UUID"));

        verifyNoInteractions(organizationRepository, applicationRepository, environmentRepository);
    }

    @Test
    void validate_shortCircuitsApplicationAndEnvironmentChecks_whenOrganizationDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn("Organization not found");

        assertThatThrownBy(() -> validator.validate(validContext()))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, "Organization not found"));

        verify(organizationRepository).findById(ORGANIZATION_ID);
        verifyNoInteractions(applicationRepository, environmentRepository);
    }

    @Test
    void validate_shortCircuitsEnvironmentChecks_whenApplicationDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(applicationRepository.findById(APPLICATION_ID.toString())).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_157.getCode()))
                .thenReturn("Application not found");

        assertThatThrownBy(() -> validator.validate(validContext()))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, "Application not found"));

        verifyNoInteractions(environmentRepository);
    }

    @Test
    void validate_throwsNotFound_whenEnvironmentDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(applicationRepository.findById(APPLICATION_ID.toString()))
                .thenReturn(Optional.of(application(ORGANIZATION_ID)));
        when(environmentRepository.findById(ENVIRONMENT_ID.toString())).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_158.getCode()))
                .thenReturn("Environment not found");

        assertThatThrownBy(() -> validator.validate(validContext()))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, "Environment not found"));
    }

    @Test
    void validate_throwsConflict_whenApplicationBelongsToAnotherOrganization() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(applicationRepository.findById(APPLICATION_ID.toString()))
                .thenReturn(Optional.of(application(OTHER_ORGANIZATION_ID)));
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_159.getCode()))
                .thenReturn("Application hierarchy conflict");

        assertThatThrownBy(() -> validator.validate(validContext()))
                .isInstanceOf(ConflictException.class)
                .satisfies(exception -> assertThat((ConflictException) exception)
                        .extracting(ConflictException::getHttpStatus, ConflictException::getUserMessage)
                        .containsExactly(409, "Application hierarchy conflict"));
    }

    @Test
    void validate_throwsConflict_whenEnvironmentBelongsToAnotherApplication() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        when(applicationRepository.findById(APPLICATION_ID.toString()))
                .thenReturn(Optional.of(application(ORGANIZATION_ID)));
        when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(OTHER_APPLICATION_ID)));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_160.getCode()))
                .thenReturn("Environment hierarchy conflict");

        assertThatThrownBy(() -> validator.validate(validContext()))
                .isInstanceOf(ConflictException.class)
                .satisfies(exception -> assertThat((ConflictException) exception)
                        .extracting(ConflictException::getHttpStatus, ConflictException::getUserMessage)
                        .containsExactly(409, "Environment hierarchy conflict"));
    }

    @Test
    void validate_trimsIdentifiersBeforeApplyingTheRules() {
        stubExistingHierarchy();
        SelectActiveContextDTO context = new SelectActiveContextDTO(
                "  " + ORGANIZATION_ID + "  ", APPLICATION_ID.toString(), ENVIRONMENT_ID.toString());

        validator.validate(context);

        verify(organizationRepository).findById(ORGANIZATION_ID);
    }

    private void stubExistingHierarchy() {
        lenient().when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization()));
        lenient().when(applicationRepository.findById(APPLICATION_ID.toString()))
                .thenReturn(Optional.of(application(ORGANIZATION_ID)));
        lenient().when(environmentRepository.findById(ENVIRONMENT_ID.toString()))
                .thenReturn(Optional.of(environment(APPLICATION_ID)));
    }

    private static SelectActiveContextDTO validContext() {
        return SelectActiveContextDTO.builder()
                .organizationId(ORGANIZATION_ID.toString())
                .applicationId(APPLICATION_ID.toString())
                .environmentId(ENVIRONMENT_ID.toString())
                .build();
    }

    private static OrganizationEntity organization() {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(ORGANIZATION_ID);
        organization.setName("Organization");
        return organization;
    }

    private static ApplicationData application(UUID organizationId) {
        return ApplicationData.build(APPLICATION_ID, "Application", organization(organizationId));
    }

    private static OrganizationEntity organization(UUID id) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(id);
        organization.setName("Organization");
        return organization;
    }

    private static EnvironmentData environment(UUID applicationId) {
        return new EnvironmentData(ENVIRONMENT_ID, "Environment", ApplicationData.build(applicationId, "Application"));
    }
}

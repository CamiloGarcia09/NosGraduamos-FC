package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.crosscutting.exceptions.ConflictException;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectActiveContextHierarchyRuleImplTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID APPLICATION_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
    private static final UUID ENVIRONMENT_ID = UUID.fromString("30000000-0000-0000-0000-000000000003");
    private final OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
    private final ApplicationRepository applicationRepository = mock(ApplicationRepository.class);
    private final EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
    private final CatalogPort catalogPort = mock(CatalogPort.class);
    private final SelectActiveContextHierarchyRuleImpl rule = new SelectActiveContextHierarchyRuleImpl(
            organizationRepository, applicationRepository, environmentRepository, catalogPort);
    private final SelectActiveContextDTO context = new SelectActiveContextDTO(
            ORGANIZATION_ID.toString(), APPLICATION_ID.toString(), ENVIRONMENT_ID.toString());
    private OrganizationEntity organization;
    private ApplicationData application;
    private EnvironmentData environment;

    @BeforeEach
    void setUp() {
        organization = organization(ORGANIZATION_ID);
        application = ApplicationData.build(APPLICATION_ID, "Application", organization);
        environment = new EnvironmentData(ENVIRONMENT_ID, "Environment", application);
    }

    @Test
    void validate_acceptsExistingConsistentHierarchy() {
        stubExistingHierarchy();

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();
    }

    @Test
    void validate_throwsNotFoundAndStopsWhenOrganizationDoesNotExist() {
        when(catalogPort.getMessage("FUN_156")).thenReturn("Organization not found");
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(NotFoundException.class)
                .extracting("userMessage").isEqualTo("Organization not found");
        verify(applicationRepository, never()).findById(context.getApplicationId());
    }

    @Test
    void validate_throwsNotFoundWhenApplicationDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(applicationRepository.findById(context.getApplicationId())).thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_157")).thenReturn("Application not found");

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(NotFoundException.class)
                .extracting("userMessage").isEqualTo("Application not found");
        verify(environmentRepository, never()).findById(context.getEnvironmentId());
    }

    @Test
    void validate_throwsNotFoundWhenEnvironmentDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(applicationRepository.findById(context.getApplicationId())).thenReturn(Optional.of(application));
        when(environmentRepository.findById(context.getEnvironmentId())).thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_158")).thenReturn("Environment not found");

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(NotFoundException.class)
                .extracting("userMessage").isEqualTo("Environment not found");
    }

    @Test
    void validate_throwsConflictWhenApplicationBelongsToAnotherOrganization() {
        application.setOrganization(organization(UUID.randomUUID()));
        environment.setApplication(application);
        stubExistingHierarchy();
        when(catalogPort.getMessage("FUN_159")).thenReturn("Application hierarchy conflict");

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(ConflictException.class)
                .extracting("userMessage").isEqualTo("Application hierarchy conflict");
    }

    @Test
    void validate_throwsConflictWhenEnvironmentBelongsToAnotherApplication() {
        environment.setApplication(ApplicationData.build(UUID.randomUUID(), "Other", organization));
        stubExistingHierarchy();
        when(catalogPort.getMessage("FUN_160")).thenReturn("Environment hierarchy conflict");

        assertThatThrownBy(() -> rule.validate(context)).isInstanceOf(ConflictException.class)
                .extracting("userMessage").isEqualTo("Environment hierarchy conflict");
    }

    private void stubExistingHierarchy() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
        when(applicationRepository.findById(context.getApplicationId())).thenReturn(Optional.of(application));
        when(environmentRepository.findById(context.getEnvironmentId())).thenReturn(Optional.of(environment));
    }

    private static OrganizationEntity organization(UUID id) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(id);
        entity.setName("Organization");
        return entity;
    }
}

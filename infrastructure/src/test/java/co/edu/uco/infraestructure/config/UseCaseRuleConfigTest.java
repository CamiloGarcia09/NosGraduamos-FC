package co.edu.uco.infraestructure.config;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.primaryports.facade.application.CreateApplicationUseCaseFacade;
import co.edu.uco.application.primaryports.facade.context.ActiveContextUseCaseFacade;
import co.edu.uco.application.primaryports.facade.catalog.FindCatalogUseCaseFacade;
import co.edu.uco.application.primaryports.facade.organization.CreateOrganizationUseCaseFacade;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.cache.ActiveContextCachePort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.secondaryports.repository.ActiveContextRepository;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.ExternalIdentityRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageCategoryCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageEnvironmentStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageTypeCatalogRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.application.usecase.handling.HandlingCreateOrganizationPort;
import co.edu.uco.application.usecase.handling.HandlingActiveContextPort;
import co.edu.uco.application.usecase.handling.HandlingCreateApplicationPort;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import co.edu.uco.application.usecase.security.MessageEnvironmentResolver;
import co.edu.uco.application.usecase.security.MessageEnvironmentResolverImpl;
import co.edu.uco.application.usecase.FindCatalogUseCase;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRule;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRuleImpl;
import co.edu.uco.application.usecase.validator.authorization.ExternalIdentityRequiredRule;
import co.edu.uco.application.usecase.validator.authorization.ExternalIdentityRequiredRuleImpl;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
import co.edu.uco.application.usecase.validator.application.CreateApplicationOrganizationExistsRule;
import co.edu.uco.application.usecase.validator.application.CreateApplicationOrganizationExistsRuleImpl;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextCompositeValidator;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextHierarchyRule;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextHierarchyRuleImpl;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextIdentifiersRule;
import co.edu.uco.application.usecase.validator.context.SelectActiveContextIdentifiersRuleImpl;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationCompositeValidator;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationNameRule;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationNameRuleImpl;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationUniqueNameRule;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationUniqueNameRuleImpl;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UseCaseRuleConfigTest {

    private static final String LANGUAGE_ID = "123e4567-e89b-12d3-a456-426614175601";
    private static final String STATE_ID = "123e4567-e89b-12d3-a456-426614175602";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private CreateOrganizationNameRule nameRule;
    @Mock
    private CreateOrganizationUniqueNameRule uniqueNameRule;
    @Mock
    private CreateOrganizationCompositeValidator validator;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private HandlingCreateOrganizationPort handlingPort;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private RecordExistsCatalogPort recordExistsCatalogPort;
    @Mock
    private UUIDValidator uuidValidator;
    @Mock
    private DateValidValidator dateValidValidator;
    @Mock
    private CreateApplicationOrganizationExistsRule organizationExistsRule;
    @Mock
    private CreateApplicationCompositeValidator applicationValidator;
    @Mock
    private HandlingCreateApplicationPort handlingApplicationPort;
    @Mock
    private AuthorizationQueryPort authorizationQueryPort;
    @Mock
    private AuthorizationRule authorizationRule;
    @Mock
    private ApplicationCatalogRepository applicationCatalogRepository;
    @Mock
    private EnvironmentCatalogRepository environmentCatalogRepository;
    @Mock
    private FunctionalityCatalogRepository functionalityCatalogRepository;
    @Mock
    private MessageTypeCatalogRepository messageTypeCatalogRepository;
    @Mock
    private MessageCategoryCatalogRepository messageCategoryCatalogRepository;
    @Mock
    private MessageStateCatalogRepository messageStateCatalogRepository;
    @Mock
    private MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository;
    @Mock
    private HandlingFindCatalogPort handlingFindCatalogPort;
    @Mock
    private ActiveContextRepository activeContextRepository;
    @Mock
    private ActiveContextCachePort activeContextCachePort;
    @Mock
    private ExternalIdentityRepository externalIdentityRepository;
    @Mock
    private EnvironmentRepository environmentRepository;
    @Mock
    private ExternalIdentityRequiredRule externalIdentityRequiredRule;
    @Mock
    private SelectActiveContextIdentifiersRule contextIdentifiersRule;
    @Mock
    private SelectActiveContextHierarchyRule contextHierarchyRule;
    @Mock
    private SelectActiveContextCompositeValidator activeContextValidator;
    @Mock
    private HandlingActiveContextPort handlingActiveContextPort;
    @Mock
    private Clock clock;

    private UseCaseRuleConfig config;

    @BeforeEach
    void setUp() {
        config = new UseCaseRuleConfig();
    }

    @Test
    void createOrganizationNameRule_instantiatesRuleConnectedToCatalog() {
        when(catalogPort.getMessage("FUN_147")).thenReturn("Nombre requerido");
        CreateOrganizationNameRule rule = config.createOrganizationNameRule(catalogPort);

        assertThat(rule).isInstanceOf(CreateOrganizationNameRuleImpl.class);
        assertThatThrownBy(() -> rule.validate(CreateOrganizationDTO.builder().name("").build()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo("Nombre requerido");
        verify(catalogPort).getMessage("FUN_147");
    }

    @Test
    void createOrganizationUniqueNameRule_instantiatesRuleConnectedToRepository() {
        when(organizationRepository.findByName("UCO")).thenReturn(Optional.empty());
        CreateOrganizationUniqueNameRule rule =
                config.createOrganizationUniqueNameRule(organizationRepository, catalogPort);

        assertThat(rule).isInstanceOf(CreateOrganizationUniqueNameRuleImpl.class);
        assertThatCode(() -> rule.validate(CreateOrganizationDTO.builder().name("UCO").build()))
                .doesNotThrowAnyException();
        verify(organizationRepository).findByName("UCO");
    }

    @Test
    void createOrganizationCompositeValidator_connectsRulesInOrder() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("UCO").build();
        CreateOrganizationCompositeValidator composite =
                config.createOrganizationCompositeValidator(catalogPort, nameRule, uniqueNameRule);

        composite.validate(dto);

        InOrder orderedRules = inOrder(nameRule, uniqueNameRule);
        orderedRules.verify(nameRule).validate(dto);
        orderedRules.verify(uniqueNameRule).validate(dto);
    }

    @Test
    void handlingCreateOrganizationPort_connectsValidatorRepositoryAndLogger() {
        when(loggerFactory.getLogger(any())).thenReturn(log);
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("UCO").build();
        HandlingCreateOrganizationPort port = config.handlingCreateOrganizationPort(
                organizationRepository, validator, loggerFactory);

        port.createOrganization(dto);

        verify(validator).validate(dto);
        verify(organizationRepository).create(any(OrganizationEntity.class));
        verify(log).info("Organization created successfully");
    }

    @Test
    void createOrganizationUseCaseFacade_connectsHandlingPort() {
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name("UCO").build();
        CreateOrganizationUseCaseFacade facade = config.createOrganizationUseCaseFacade(handlingPort);

        facade.execute(dto);

        verify(handlingPort).createOrganization(dto);
    }

    @Test
    void createApplicationOrganizationExistsRule_connectsUuidValidatorAndRepository() {
        UUID organizationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(new OrganizationEntity()));
        CreateApplicationOrganizationExistsRule rule = config.createApplicationOrganizationExistsRule(
                organizationRepository, uuidValidator, catalogPort);
        CreateApplicationDTO dto = validApplicationDto();

        assertThatCode(() -> rule.validate(dto)).doesNotThrowAnyException();

        assertThat(rule).isInstanceOf(CreateApplicationOrganizationExistsRuleImpl.class);
        verify(uuidValidator).validate(organizationId.toString());
        verify(organizationRepository).findById(organizationId);
    }

    @Test
    void createApplicationCompositeValidator_connectsOrganizationAndExistingRules() {
        when(recordExistsCatalogPort.exists(any(), any())).thenReturn(true);
        when(applicationRepository.findByName("Messages")).thenReturn(Optional.empty());
        CreateApplicationCompositeValidator composite = config.createApplicationCompositeValidator(
                catalogPort, recordExistsCatalogPort, applicationRepository,
                dateValidValidator, organizationExistsRule);
        CreateApplicationDTO dto = validApplicationDto();

        composite.validate(dto);

        verify(organizationExistsRule).validate(dto);
        verify(dateValidValidator).validate(dto.getStartDate());
        verify(dateValidValidator).validate(dto.getEndDate());
        verify(applicationRepository).findByName("Messages");
    }

    @Test
    void handlingCreateApplicationPort_connectsValidatorRepositoryAndLogger() {
        when(loggerFactory.getLogger(any())).thenReturn(log);
        CreateApplicationDTO dto = validApplicationDto();
        HandlingCreateApplicationPort port = config.handlingCreateApplicationPort(
                applicationRepository, applicationValidator, handlingActiveContextPort,
                authorizationRule, catalogPort, loggerFactory);

        port.createApplication(dto, null);

        verify(applicationValidator).validate(dto);
        verify(applicationRepository).create(any(ApplicationData.class),
                eq(LANGUAGE_ID), any(), any(), eq(STATE_ID));
        verify(log).info("Application created successfully with name: {}", "Messages");
        verifyNoInteractions(handlingActiveContextPort, authorizationRule);
    }

    @Test
    void createApplicationUseCaseFacade_connectsHandlingPort() {
        CreateApplicationDTO dto = validApplicationDto();
        CreateApplicationUseCaseFacade facade = config.createApplicationUseCaseFacade(handlingApplicationPort);

        facade.execute(dto, null);

        verify(handlingApplicationPort).createApplication(dto, null);
    }

    @Test
    void authorizationRule_createsFrameworkFreeImplementation() {
        assertThat(config.authorizationRule(authorizationQueryPort, catalogPort))
                .isInstanceOf(AuthorizationRuleImpl.class);
    }

    @Test
    void externalIdentityRequiredRule_usesAuthenticationCatalogMessage() {
        when(catalogPort.getMessage("FUN_152")).thenReturn("Authentication required");
        ExternalIdentityRequiredRule rule = config.externalIdentityRequiredRule(catalogPort);

        assertThat(rule).isInstanceOf(ExternalIdentityRequiredRuleImpl.class);
        assertThatThrownBy(() -> rule.validate(null))
                .isInstanceOf(UnauthorizedException.class)
                .extracting("userMessage")
                .isEqualTo("Authentication required");
    }

    @Test
    void selectActiveContextIdentifiersRule_wiresUuidValidation() {
        SelectActiveContextIdentifiersRule rule = config.selectActiveContextIdentifiersRule(uuidValidator, catalogPort);
        SelectActiveContextDTO context = contextSelection();

        rule.validate(context);

        assertThat(rule).isInstanceOf(SelectActiveContextIdentifiersRuleImpl.class);
        verify(uuidValidator).validate(context.getOrganizationId());
        verify(uuidValidator).validate(context.getApplicationId());
        verify(uuidValidator).validate(context.getEnvironmentId());
    }

    @Test
    void selectActiveContextHierarchyRule_wiresRepositories() {
        UUID organizationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID applicationId = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setId(organizationId);
        ApplicationData application = ApplicationData.build(applicationId, "App", organization);
        co.edu.uco.application.secondaryports.entity.EnvironmentData environment =
                new co.edu.uco.application.secondaryports.entity.EnvironmentData(
                        UUID.fromString("323e4567-e89b-12d3-a456-426614174000"), "Dev", application);
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(applicationRepository.findById(applicationId.toString())).thenReturn(Optional.of(application));
        when(environmentRepository.findById(environment.getId().toString())).thenReturn(Optional.of(environment));
        SelectActiveContextHierarchyRule rule = config.selectActiveContextHierarchyRule(
                organizationRepository, applicationRepository, environmentRepository, catalogPort);

        assertThatCode(() -> rule.validate(contextSelection())).doesNotThrowAnyException();
        assertThat(rule).isInstanceOf(SelectActiveContextHierarchyRuleImpl.class);
    }

    @Test
    void selectActiveContextCompositeValidator_connectsRulesInOrder() {
        SelectActiveContextDTO context = contextSelection();
        SelectActiveContextCompositeValidator composite = config.selectActiveContextCompositeValidator(
                contextIdentifiersRule, contextHierarchyRule);

        composite.validate(context);

        InOrder orderedRules = inOrder(contextIdentifiersRule, contextHierarchyRule);
        orderedRules.verify(contextIdentifiersRule).validate(context);
        orderedRules.verify(contextHierarchyRule).validate(context);
    }

    @Test
    void handlingActiveContextPort_wiresAllUseCaseDependencies() {
        HandlingActiveContextPort port = config.handlingActiveContextPort(
                activeContextRepository, activeContextCachePort, externalIdentityRepository,
                applicationCatalogRepository, environmentCatalogRepository, authorizationQueryPort,
                authorizationRule, externalIdentityRequiredRule, activeContextValidator, catalogPort, clock);

        assertThat(port).isInstanceOf(co.edu.uco.application.usecase.ActiveContextUseCase.class);
    }

    @Test
    void activeContextUseCaseFacade_connectsHandlingPort() {
        ActiveContextUseCaseFacade facade = config.activeContextUseCaseFacade(handlingActiveContextPort);

        facade.findAvailableContexts(null);

        verify(handlingActiveContextPort).findAvailableContexts(null);
    }

    @Test
    void messageEnvironmentResolver_connectsActiveContextAndAuthorization() {
        MessageEnvironmentResolver resolver = config.messageEnvironmentResolver(
                handlingActiveContextPort, authorizationRule, catalogPort);

        assertThat(resolver).isInstanceOf(MessageEnvironmentResolverImpl.class);
    }

    @Test
    void handlingFindCatalogPort_wiresCatalogRepositoriesAndAuthorization() {
        HandlingFindCatalogPort port = config.handlingFindCatalogPort(
                applicationCatalogRepository,
                environmentCatalogRepository,
                functionalityCatalogRepository,
                messageTypeCatalogRepository,
                messageCategoryCatalogRepository,
                messageStateCatalogRepository,
                messageEnvironmentStateCatalogRepository,
                authorizationQueryPort,
                authorizationRule,
                handlingActiveContextPort,
                catalogPort);

        assertThat(port).isInstanceOf(FindCatalogUseCase.class);
    }

    @Test
    void findCatalogUseCaseFacade_connectsHandlingPort() {
        FindCatalogUseCaseFacade facade = config.findCatalogUseCaseFacade(handlingFindCatalogPort);

        facade.findApplications(null);

        verify(handlingFindCatalogPort).findApplications(null);
    }

    private CreateApplicationDTO validApplicationDto() {
        return CreateApplicationDTO.builder()
                .name("Messages")
                .organizationId("123e4567-e89b-12d3-a456-426614174000")
                .languageId(LANGUAGE_ID)
                .startDate("2025-01-01T00:00:00")
                .endDate("2025-12-31T23:59:59")
                .stateId(STATE_ID)
                .build();
    }

    private SelectActiveContextDTO contextSelection() {
        return SelectActiveContextDTO.builder()
                .organizationId("123e4567-e89b-12d3-a456-426614174000")
                .applicationId("223e4567-e89b-12d3-a456-426614174000")
                .environmentId("323e4567-e89b-12d3-a456-426614174000")
                .build();
    }
}

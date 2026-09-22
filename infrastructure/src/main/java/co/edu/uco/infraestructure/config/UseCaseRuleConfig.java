package co.edu.uco.infraestructure.config;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageCategoryCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageEnvironmentStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageTypeCatalogRepository;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.security.AuthorizationQueryPort;
import co.edu.uco.application.primaryports.facade.application.CreateApplicationUseCaseFacade;
import co.edu.uco.application.primaryports.facade.application.impl.CreateApplicationUseCaseFacadeImpl;
import co.edu.uco.application.primaryports.facade.catalog.FindCatalogUseCaseFacade;
import co.edu.uco.application.primaryports.facade.catalog.impl.FindCatalogUseCaseFacadeImpl;
import co.edu.uco.application.primaryports.facade.organization.CreateOrganizationUseCaseFacade;
import co.edu.uco.application.primaryports.facade.organization.impl.CreateOrganizationUseCaseFacadeImpl;
import co.edu.uco.application.usecase.CreateApplicationUseCase;
import co.edu.uco.application.usecase.FindCatalogUseCase;
import co.edu.uco.application.usecase.CreateOrganizationUseCase;
import co.edu.uco.application.usecase.handling.HandlingCreateApplicationPort;
import co.edu.uco.application.usecase.handling.HandlingCreateOrganizationPort;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRule;
import co.edu.uco.application.usecase.validator.authorization.AuthorizationRuleImpl;
import co.edu.uco.application.usecase.validator.application.CreateApplicationCompositeValidator;
import co.edu.uco.application.usecase.validator.application.CreateApplicationOrganizationExistsRule;
import co.edu.uco.application.usecase.validator.application.CreateApplicationOrganizationExistsRuleImpl;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.application.usecase.validator.message.CreateMessageContextRule;
import co.edu.uco.application.usecase.validator.message.CreateMessageContextRuleImpl;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationCompositeValidator;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationNameRule;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationNameRuleImpl;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationUniqueNameRule;
import co.edu.uco.application.usecase.validator.organization.CreateOrganizationUniqueNameRuleImpl;
import co.edu.uco.application.usecase.validator.token.DateValidValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseRuleConfig {

    @Bean
    AuthorizationRule authorizationRule(AuthorizationQueryPort authorizationQueryPort, CatalogPort catalogPort) {
        return new AuthorizationRuleImpl(authorizationQueryPort, catalogPort);
    }

    @Bean
    HandlingFindCatalogPort handlingFindCatalogPort(
            ApplicationCatalogRepository applicationCatalogRepository,
            EnvironmentCatalogRepository environmentCatalogRepository,
            FunctionalityCatalogRepository functionalityCatalogRepository,
            MessageTypeCatalogRepository messageTypeCatalogRepository,
            MessageCategoryCatalogRepository messageCategoryCatalogRepository,
            MessageStateCatalogRepository messageStateCatalogRepository,
            MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository,
            AuthorizationQueryPort authorizationQueryPort,
            AuthorizationRule authorizationRule) {
        return new FindCatalogUseCase(
                applicationCatalogRepository,
                environmentCatalogRepository,
                functionalityCatalogRepository,
                messageTypeCatalogRepository,
                messageCategoryCatalogRepository,
                messageStateCatalogRepository,
                messageEnvironmentStateCatalogRepository,
                authorizationQueryPort,
                authorizationRule);
    }

    @Bean
    FindCatalogUseCaseFacade findCatalogUseCaseFacade(HandlingFindCatalogPort handlingFindCatalogPort) {
        return new FindCatalogUseCaseFacadeImpl(handlingFindCatalogPort);
    }

    @Bean
    CreateMessageContextRule createMessageContextRule(EnvironmentRepository environmentRepository,
                                                      FunctionalityCatalogRepository functionalityCatalogRepository,
                                                      CatalogPort catalogPort) {
        return new CreateMessageContextRuleImpl(
                environmentRepository,
                functionalityCatalogRepository,
                catalogPort);
    }

    @Bean
    CreateOrganizationNameRule createOrganizationNameRule(CatalogPort catalogPort) {
        return new CreateOrganizationNameRuleImpl(catalogPort);
    }

    @Bean
    CreateOrganizationUniqueNameRule createOrganizationUniqueNameRule(
            OrganizationRepository organizationRepository, CatalogPort catalogPort) {
        return new CreateOrganizationUniqueNameRuleImpl(organizationRepository, catalogPort);
    }

    @Bean
    CreateOrganizationCompositeValidator createOrganizationCompositeValidator(
            CatalogPort catalogPort,
            CreateOrganizationNameRule createOrganizationNameRule,
            CreateOrganizationUniqueNameRule createOrganizationUniqueNameRule) {
        return new CreateOrganizationCompositeValidator(
                catalogPort, createOrganizationNameRule, createOrganizationUniqueNameRule);
    }

    @Bean
    HandlingCreateOrganizationPort handlingCreateOrganizationPort(
            OrganizationRepository organizationRepository,
            CreateOrganizationCompositeValidator validator,
            LoggingPortFactory loggerFactory) {
        return new CreateOrganizationUseCase(organizationRepository, validator, loggerFactory);
    }

    @Bean
    CreateOrganizationUseCaseFacade createOrganizationUseCaseFacade(
            HandlingCreateOrganizationPort handlingCreateOrganizationPort) {
        return new CreateOrganizationUseCaseFacadeImpl(handlingCreateOrganizationPort);
    }

    @Bean
    CreateApplicationOrganizationExistsRule createApplicationOrganizationExistsRule(
            OrganizationRepository organizationRepository,
            UUIDValidator uuidValidator,
            CatalogPort catalogPort) {
        return new CreateApplicationOrganizationExistsRuleImpl(
                organizationRepository, uuidValidator, catalogPort);
    }

    @Bean
    CreateApplicationCompositeValidator createApplicationCompositeValidator(
            CatalogPort catalogPort,
            RecordExistsCatalogPort recordExistsCatalogPort,
            ApplicationRepository applicationRepository,
            DateValidValidator dateValidValidator,
            CreateApplicationOrganizationExistsRule organizationExistsRule) {
        return new CreateApplicationCompositeValidator(
                catalogPort,
                recordExistsCatalogPort,
                applicationRepository,
                dateValidValidator,
                organizationExistsRule);
    }

    @Bean
    HandlingCreateApplicationPort handlingCreateApplicationPort(
            ApplicationRepository applicationRepository,
            CreateApplicationCompositeValidator validator,
            LoggingPortFactory loggerFactory) {
        return new CreateApplicationUseCase(applicationRepository, validator, loggerFactory);
    }

    @Bean
    CreateApplicationUseCaseFacade createApplicationUseCaseFacade(
            HandlingCreateApplicationPort handlingCreateApplicationPort) {
        return new CreateApplicationUseCaseFacadeImpl(handlingCreateApplicationPort);
    }
}

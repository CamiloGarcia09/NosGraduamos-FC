package co.edu.uco.infraestructure.config;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.usecase.validator.message.CreateMessageContextRule;
import co.edu.uco.application.usecase.validator.message.CreateMessageContextRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseRuleConfig {

    @Bean
    CreateMessageContextRule createMessageContextRule(EnvironmentRepository environmentRepository,
                                                      FunctionalityCatalogRepository functionalityCatalogRepository,
                                                      CatalogPort catalogPort) {
        return new CreateMessageContextRuleImpl(
                environmentRepository,
                functionalityCatalogRepository,
                catalogPort);
    }
}

package co.edu.uco.application.usecase.validator.token;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import org.springframework.stereotype.Component;

import co.edu.uco.application.usecase.validator.token.rule.TokenApplicationBelongsEnvironmentRule;
import co.edu.uco.application.usecase.validator.token.rule.TokenApplicationUuidRule;
import co.edu.uco.application.usecase.validator.token.rule.TokenEnvironmentExistsRule;
import co.edu.uco.application.usecase.validator.token.rule.TokenEnvironmentUuidRule;
import co.edu.uco.application.usecase.validator.token.rule.TokenExpirationDateValidRule;
import co.edu.uco.application.usecase.validator.token.rule.TokenExpirationNotPassedRule;

import java.time.Clock;
import java.util.List;

@Component
public final class CreateTokenCompositeValidator extends CompositeValidator<TokenValidationContext> {

    public CreateTokenCompositeValidator(CatalogPort catalogPort, EnvironmentRepository environmentRepository, Clock clock) {
        super(List.of(
                new TokenApplicationUuidRule(catalogPort),
                new TokenEnvironmentUuidRule(catalogPort),
                new TokenExpirationDateValidRule(catalogPort),
                new TokenExpirationNotPassedRule(catalogPort, clock),
                new TokenEnvironmentExistsRule(catalogPort, environmentRepository),
                new TokenApplicationBelongsEnvironmentRule(catalogPort, environmentRepository)
        ), catalogPort);
    }
}
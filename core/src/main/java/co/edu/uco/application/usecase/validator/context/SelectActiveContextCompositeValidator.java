package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextApplicationBelongsOrganizationRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextApplicationExistsRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextApplicationIdUuidRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextEnvironmentBelongsApplicationRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextEnvironmentExistsRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextEnvironmentIdUuidRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextIdentifiersRequiredRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextOrganizationExistsRule;
import co.edu.uco.application.usecase.validator.context.rule.SelectActiveContextOrganizationIdUuidRule;

import java.util.List;

public final class SelectActiveContextCompositeValidator extends CompositeValidator<SelectActiveContextDTO> {

    public SelectActiveContextCompositeValidator(final CatalogPort catalogPort,
                                                 final OrganizationRepository organizationRepository,
                                                 final ApplicationRepository applicationRepository,
                                                 final EnvironmentRepository environmentRepository) {
        super(List.of(
                new SelectActiveContextIdentifiersRequiredRule(catalogPort),
                new SelectActiveContextOrganizationIdUuidRule(catalogPort),
                new SelectActiveContextApplicationIdUuidRule(catalogPort),
                new SelectActiveContextEnvironmentIdUuidRule(catalogPort),
                new SelectActiveContextOrganizationExistsRule(catalogPort, organizationRepository),
                new SelectActiveContextApplicationExistsRule(catalogPort, applicationRepository),
                new SelectActiveContextEnvironmentExistsRule(catalogPort, environmentRepository),
                new SelectActiveContextApplicationBelongsOrganizationRule(catalogPort, applicationRepository),
                new SelectActiveContextEnvironmentBelongsApplicationRule(catalogPort, environmentRepository)
        ), catalogPort);
    }

    @Override
    public void validate(final SelectActiveContextDTO context) {
        super.validate(context == null ? new SelectActiveContextDTO() : context);
    }
}

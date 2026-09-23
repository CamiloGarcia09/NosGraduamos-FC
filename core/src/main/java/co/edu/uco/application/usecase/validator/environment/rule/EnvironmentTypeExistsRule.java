package co.edu.uco.application.usecase.validator.environment.rule;

import co.edu.uco.application.primaryports.dto.environment.CreateEnvironmentDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.application.usecase.validator.specification.Specifications;
import co.edu.uco.application.usecase.validator.specification.impl.CatalogReferenceExistsSpecification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;

public final class EnvironmentTypeExistsRule extends RuleValidator<CreateEnvironmentDTO> {

    public EnvironmentTypeExistsRule(CatalogPort catalogPort, RecordExistsCatalogPort recordExistsCatalogPort) {
        super(catalogPort,
                Specifications.field(CreateEnvironmentDTO::getTypeId,
                        new CatalogReferenceExistsSpecification(recordExistsCatalogPort, ReferenceCatalog.ENVIRONMENT_TYPE)),
                MessageCatalogCodeEnum.FUN_159);
    }
}
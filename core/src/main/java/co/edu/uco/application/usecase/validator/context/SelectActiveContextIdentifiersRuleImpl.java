package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

public final class SelectActiveContextIdentifiersRuleImpl implements SelectActiveContextIdentifiersRule {

    private final UUIDValidator uuidValidator;
    private final CatalogPort catalogPort;

    public SelectActiveContextIdentifiersRuleImpl(final UUIDValidator uuidValidator, final CatalogPort catalogPort) {
        this.uuidValidator = uuidValidator;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final SelectActiveContextDTO context) {
        if (context == null || isEmptyOrNull(context.getOrganizationId())
                || isEmptyOrNull(context.getApplicationId()) || isEmptyOrNull(context.getEnvironmentId())) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_155.getCode()));
        }
        uuidValidator.validate(context.getOrganizationId());
        uuidValidator.validate(context.getApplicationId());
        uuidValidator.validate(context.getEnvironmentId());
    }
}

package co.edu.uco.application.usecase.validator.message.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.message.CreateMessageValidationContext;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;
import static co.edu.uco.crosscutting.helpers.UtilUUID.isEqual;

public final class MessageAuthenticatedEnvironmentRule extends RuleValidator<CreateMessageValidationContext> {

    public MessageAuthenticatedEnvironmentRule(CatalogPort catalogPort) {
        super(catalogPort,
                context -> isAuthorized(context.dto().getEnvironmentId(), context.authenticatedEnvironmentId()),
                MessageCatalogCodeEnum.FUN_145,
                ForbiddenException::buildUserException);
    }

    private static boolean isAuthorized(String requestedEnvironmentId, String authenticatedEnvironmentId) {
        return !isEmptyOrNull(authenticatedEnvironmentId)
                && (isEmptyOrNull(requestedEnvironmentId)
                || isEqual(getUUIDFromString(authenticatedEnvironmentId), getUUIDFromString(requestedEnvironmentId)));
    }
}

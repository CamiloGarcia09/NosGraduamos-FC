package co.edu.uco.application.usecase.validator.message.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.usecase.validator.message.CreateMessageValidationContext;
import co.edu.uco.application.usecase.validator.rule.RuleValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;

import static co.edu.uco.crosscutting.helpers.UtilUUID.getStringFromUUID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getUUIDFromString;

public final class MessageEnvironmentExistsRule extends RuleValidator<CreateMessageValidationContext> {

    public MessageEnvironmentExistsRule(CatalogPort catalogPort, EnvironmentRepository environmentRepository) {
        super(catalogPort,
                context -> environmentRepository.findById(normalizedEnvironmentId(context)).isPresent(),
                MessageCatalogCodeEnum.FUN_035,
                ForbiddenException::buildUserException);
    }

    private static String normalizedEnvironmentId(CreateMessageValidationContext context) {
        return getStringFromUUID(getUUIDFromString(context.authenticatedEnvironmentId()));
    }
}

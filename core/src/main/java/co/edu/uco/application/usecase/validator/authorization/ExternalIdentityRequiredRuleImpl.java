package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.UnauthorizedException;

public final class ExternalIdentityRequiredRuleImpl implements ExternalIdentityRequiredRule {

    private final CatalogPort catalogPort;

    public ExternalIdentityRequiredRuleImpl(final CatalogPort catalogPort) {
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(final ExternalIdentity identity) {
        if (identity == null) {
            throw UnauthorizedException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_152.getCode()));
        }
    }
}

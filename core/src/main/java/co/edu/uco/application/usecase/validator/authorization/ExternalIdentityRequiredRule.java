package co.edu.uco.application.usecase.validator.authorization;

import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface ExternalIdentityRequiredRule {

    void validate(ExternalIdentity identity);
}

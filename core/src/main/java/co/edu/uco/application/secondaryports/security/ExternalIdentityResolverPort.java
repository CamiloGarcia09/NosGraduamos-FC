package co.edu.uco.application.secondaryports.security;

import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

public interface ExternalIdentityResolverPort {

    ExternalIdentity resolve(String accessToken);
}

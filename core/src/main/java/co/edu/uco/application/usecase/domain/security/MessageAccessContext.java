package co.edu.uco.application.usecase.domain.security;

public record MessageAccessContext(
        String legacyEnvironmentId,
        ExternalIdentity externalIdentity
) {
}

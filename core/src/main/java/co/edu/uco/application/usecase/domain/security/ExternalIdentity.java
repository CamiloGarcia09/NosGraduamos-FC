package co.edu.uco.application.usecase.domain.security;

import java.time.Instant;

public record ExternalIdentity(
        String issuer,
        String subject,
        String email,
        PrincipalType principalType,
        Instant expiration
) {
}

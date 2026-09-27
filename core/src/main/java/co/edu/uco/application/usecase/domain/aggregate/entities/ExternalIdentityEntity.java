package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.application.usecase.domain.aggregate.Entity;
import co.edu.uco.application.usecase.domain.security.PrincipalType;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;

@Getter
public final class ExternalIdentityEntity extends Entity<UUID> {

    private UUID id;
    private String issuer;
    private String subject;
    private String email;
    private PrincipalType principalType;

    @Override
    public void setId(final UUID id) {
        this.id = getDefaultUUID(id);
    }

    public void setIssuer(final String issuer) {
        this.issuer = trim(issuer);
    }

    public void setSubject(final String subject) {
        this.subject = trim(subject);
    }

    public void setEmail(final String email) {
        this.email = email == null ? null : trim(email);
    }

    public void setPrincipalType(final PrincipalType principalType) {
        this.principalType = principalType;
    }
}

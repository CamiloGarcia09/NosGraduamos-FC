package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model;

import co.edu.uco.application.usecase.domain.security.PrincipalType;
import lombok.Getter;

import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getDefaultUUID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getNewUUID;

@Getter
public final class ExternalIdentitySurrealModel {

    private UUID id;
    private String issuer;
    private String subject;
    private String email;
    private PrincipalType principalType;

    public ExternalIdentitySurrealModel(final UUID id, final String issuer, final String subject,
                                        final String email, final PrincipalType principalType) {
        setId(id);
        setIssuer(issuer);
        setSubject(subject);
        setEmail(email);
        setPrincipalType(principalType);
    }

    public ExternalIdentitySurrealModel() {
        setId(getNewUUID());
        setIssuer(EMPTY);
        setSubject(EMPTY);
        setEmail(null);
        setPrincipalType(null);
    }

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

    public static ExternalIdentitySurrealModel build() {
        return new ExternalIdentitySurrealModel();
    }
}

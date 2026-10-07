package co.edu.uco.application.usecase.domain.aggregate.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalIdentityEntityTest {

    @Test
    void setId_acceptsValidIdentifier() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        UUID id = UUID.fromString("b38d6507-b7cd-4a2f-bbed-0e6c7f434fbd");

        entity.setId(id);

        assertThat(entity.getId()).isEqualTo(id);
    }

    @Test
    void setId_usesDefaultIdentifier_whenIdentifierIsMissing() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setId(null);

        assertThat(entity.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setIssuer_trimsValue() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setIssuer("  https://accounts.google.com  ");

        assertThat(entity.getIssuer()).isEqualTo("https://accounts.google.com");
    }

    @Test
    void setIssuer_usesEmptyValue_whenIssuerIsMissing() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setIssuer(null);

        assertThat(entity.getIssuer()).isEmpty();
    }

    @Test
    void setSubject_trimsValuePreservingCase() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setSubject("  User123  ");

        assertThat(entity.getSubject()).isEqualTo("User123");
    }

    @Test
    void setSubject_usesEmptyValue_whenSubjectIsMissing() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setSubject(null);

        assertThat(entity.getSubject()).isEmpty();
    }

    @Test
    void setEmail_trimsValue() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setEmail("  user@example.com  ");

        assertThat(entity.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void setEmail_acceptsNull_whenEmailIsMissing() {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();

        entity.setEmail(null);

        assertThat(entity.getEmail()).isNull();
    }
}

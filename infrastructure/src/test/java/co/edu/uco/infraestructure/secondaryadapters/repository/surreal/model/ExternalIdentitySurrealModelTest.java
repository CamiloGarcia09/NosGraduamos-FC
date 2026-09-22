package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model;

import co.edu.uco.application.usecase.domain.security.PrincipalType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalIdentitySurrealModelTest {

    @Test
    void defaultConstructor_initializesDefaultValues() {
        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel();

        assertThat(model.getId()).isNotNull();
        assertThat(model.getIssuer()).isEmpty();
        assertThat(model.getSubject()).isEmpty();
        assertThat(model.getEmail()).isNull();
        assertThat(model.getPrincipalType()).isNull();
    }

    @Test
    void fullConstructor_assignsNormalizedValues() {
        UUID id = UUID.randomUUID();

        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "  https://google.com  ", "  User1  ", "  user@x.com  ", PrincipalType.HUMAN);

        assertThat(model.getId()).isEqualTo(id);
        assertThat(model.getIssuer()).isEqualTo("https://google.com");
        assertThat(model.getSubject()).isEqualTo("User1");
        assertThat(model.getEmail()).isEqualTo("user@x.com");
        assertThat(model.getPrincipalType()).isEqualTo(PrincipalType.HUMAN);
    }

    @Test
    void fullConstructor_acceptsNullEmail() {
        UUID id = UUID.randomUUID();

        ExternalIdentitySurrealModel model = new ExternalIdentitySurrealModel(
                id, "issuer", "subject", null, PrincipalType.SERVICE);

        assertThat(model.getEmail()).isNull();
        assertThat(model.getPrincipalType()).isEqualTo(PrincipalType.SERVICE);
    }

    @Test
    void setters_applyDefaults_whenValuesAreMissing() {
        ExternalIdentitySurrealModel model = ExternalIdentitySurrealModel.build();

        model.setId(null);
        model.setIssuer(null);
        model.setSubject(null);
        model.setEmail(null);
        model.setPrincipalType(null);

        assertThat(model.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(model.getIssuer()).isEmpty();
        assertThat(model.getSubject()).isEmpty();
        assertThat(model.getEmail()).isNull();
        assertThat(model.getPrincipalType()).isNull();
    }

    @Test
    void build_returnsInstanceWithDefaultValues() {
        ExternalIdentitySurrealModel model = ExternalIdentitySurrealModel.build();

        assertThat(model.getId()).isNotNull();
        assertThat(model.getIssuer()).isEmpty();
        assertThat(model.getSubject()).isEmpty();
    }
}

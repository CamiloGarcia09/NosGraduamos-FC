package co.edu.uco.application.primaryports.dto.token;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDTOTest {

    private static final UUID ENV_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614175000");
    private static final LocalDateTime CREATION = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    private static final LocalDateTime EXPIRATION = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

    private TokenDTO buildToken() {
        return TokenDTO.builder()
                .id("token-1")
                .secretName("secret-name")
                .creationDate(CREATION)
                .expirationDate(EXPIRATION)
                .environmentId(ENV_ID)
                .build();
    }

    @Test
    void builder_createsDtoWithValues() {
        TokenDTO dto = buildToken();

        assertThat(dto.getId()).isEqualTo("token-1");
        assertThat(dto.getSecretName()).isEqualTo("secret-name");
        assertThat(dto.getCreationDate()).isEqualTo(CREATION);
        assertThat(dto.getExpirationDate()).isEqualTo(EXPIRATION);
        assertThat(dto.getEnvironmentId()).isEqualTo(ENV_ID);
    }

    @Test
    void setters_updateValues() {
        TokenDTO dto = new TokenDTO();
        dto.setId("token-2");
        dto.setSecretName("other-secret");
        dto.setCreationDate(EXPIRATION);
        dto.setExpirationDate(CREATION);
        dto.setEnvironmentId(null);

        assertThat(dto.getId()).isEqualTo("token-2");
        assertThat(dto.getSecretName()).isEqualTo("other-secret");
        assertThat(dto.getCreationDate()).isEqualTo(EXPIRATION);
        assertThat(dto.getExpirationDate()).isEqualTo(CREATION);
        assertThat(dto.getEnvironmentId()).isNull();
    }

    @Test
    void equals_returnsTrueForSameValuesAndSameHashCode() {
        TokenDTO a = buildToken();
        TokenDTO b = buildToken();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_returnsTrueWhenBothFieldsAreNull() {
        TokenDTO a = new TokenDTO();
        TokenDTO b = new TokenDTO();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_returnsFalseForNullAndDifferentType() {
        TokenDTO a = buildToken();

        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("token-1");
    }

    @Test
    void equals_returnsFalseWhenFieldDiffers() {
        TokenDTO a = buildToken();
        TokenDTO b = buildToken();
        b.setId("token-2");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_returnsFalseWhenOneFieldIsNullAndOtherIsNot() {
        TokenDTO a = buildToken();
        TokenDTO b = buildToken();
        b.setSecretName(null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_containsFieldValues() {
        String value = buildToken().toString();

        assertThat(value).contains("token-1", "secret-name", ENV_ID.toString());
    }
}
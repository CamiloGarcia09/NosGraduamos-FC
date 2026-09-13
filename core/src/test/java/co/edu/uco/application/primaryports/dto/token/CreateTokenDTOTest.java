package co.edu.uco.application.primaryports.dto.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTokenDTOTest {

    @Test
    void defaultConstructor_initializesEmptyFields() {
        CreateTokenDTO dto = new CreateTokenDTO();

        assertThat(dto.getExpirationDate()).isEmpty();
        assertThat(dto.getEnvironmentId()).isEmpty();
    }

    @Test
    void parameterizedConstructor_trimsValues() {
        CreateTokenDTO dto = new CreateTokenDTO("  2026-12-31T10:00:00  ", "  env-123  ");

        assertThat(dto.getExpirationDate()).isEqualTo("2026-12-31T10:00:00");
        assertThat(dto.getEnvironmentId()).isEqualTo("env-123");
    }

    @Test
    void setters_trimValues() {
        CreateTokenDTO dto = new CreateTokenDTO();
        dto.setExpirationDate("  date  ");
        dto.setEnvironmentId("  env  ");

        assertThat(dto.getExpirationDate()).isEqualTo("date");
        assertThat(dto.getEnvironmentId()).isEqualTo("env");
    }

    @Test
    void builder_createsInstance() {
        CreateTokenDTO dto = CreateTokenDTO.builder()
                .expirationDate("2026-12-31T10:00:00")
                .environmentId("env-123")
                .build();

        assertThat(dto.getExpirationDate()).isEqualTo("2026-12-31T10:00:00");
        assertThat(dto.getEnvironmentId()).isEqualTo("env-123");
    }
}
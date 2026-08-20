package co.edu.uco.infraestructure.secondaryadapters.secrets.doppler;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DopplerFindTokenDTOTest {

    @Test
    void jsonConstructor_extractsRawFromValueMap() {
        Map<String, Object> value = new HashMap<>();
        value.put("raw", "raw-content");

        DopplerFindTokenDTO dto = new DopplerFindTokenDTO("secret-1", value);

        assertThat(dto.getName()).isEqualTo("secret-1");
        assertThat(dto.getRaw()).isEqualTo("raw-content");
    }

    @Test
    void jsonConstructor_usesEmptyRawWhenValueMissing() {
        Map<String, Object> value = new HashMap<>();

        DopplerFindTokenDTO dto = new DopplerFindTokenDTO("secret-1", value);

        assertThat(dto.getRaw()).isEmpty();
    }

    @Test
    void allArgsConstructor_assignsFields() {
        DopplerFindTokenDTO dto = new DopplerFindTokenDTO("name", "raw");

        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getRaw()).isEqualTo("raw");
    }
}
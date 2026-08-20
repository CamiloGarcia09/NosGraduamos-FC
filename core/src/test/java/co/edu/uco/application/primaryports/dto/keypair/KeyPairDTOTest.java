package co.edu.uco.application.primaryports.dto.keypair;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;

class KeyPairDTOTest {

    private KeyPair generate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    @Test
    void builder_createsKeyPairDto() throws Exception {
        KeyPair pair = generate();
        KeyPairDTO dto = KeyPairDTO.builder()
                .publicKey(pair.getPublic())
                .privateKey(pair.getPrivate())
                .build();

        assertThat(dto.getPublicKey()).isEqualTo(pair.getPublic());
        assertThat(dto.getPrivateKey()).isEqualTo(pair.getPrivate());
    }

    @Test
    void allArgsConstructor_setsKeys() throws Exception {
        KeyPair pair = generate();
        KeyPairDTO dto = new KeyPairDTO(pair.getPublic(), pair.getPrivate());

        assertThat(dto.getPublicKey()).isEqualTo(pair.getPublic());
        assertThat(dto.getPrivateKey()).isEqualTo(pair.getPrivate());
    }

    @Test
    void setters_updateKeys() throws Exception {
        KeyPair pair = generate();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();
        KeyPairDTO dto = new KeyPairDTO(null, null);
        dto.setPublicKey(publicKey);
        dto.setPrivateKey(privateKey);

        assertThat(dto.getPublicKey()).isEqualTo(publicKey);
        assertThat(dto.getPrivateKey()).isEqualTo(privateKey);
    }
}
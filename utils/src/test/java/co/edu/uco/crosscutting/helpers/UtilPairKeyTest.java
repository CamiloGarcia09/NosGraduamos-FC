package co.edu.uco.crosscutting.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilPairKeyTest {

    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @Test
    void encodePrivateKey_returnsBase64String() {
        String encoded = UtilPairKey.encodePrivateKey(keyPair.getPrivate());
        assertThat(encoded).isNotBlank();
        assertThat(encoded).doesNotContain("\n");
    }

    @Test
    void decodePrivateKey_roundTripsToSameKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String encoded = UtilPairKey.encodePrivateKey(keyPair.getPrivate());
        PrivateKey decoded = UtilPairKey.decodePrivateKey(encoded);
        assertThat(decoded.getAlgorithm()).isEqualTo("RSA");
        assertThat(decoded.getEncoded()).isEqualTo(keyPair.getPrivate().getEncoded());
    }

    @Test
    void encodePublicKey_returnsBase64String() {
        String encoded = UtilPairKey.encodePublicKey(keyPair.getPublic());
        assertThat(encoded).isNotBlank();
    }

    @Test
    void decodePublicKey_roundTripsToSameKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String encoded = UtilPairKey.encodePublicKey(keyPair.getPublic());
        PublicKey decoded = UtilPairKey.decodePublicKey(encoded);
        assertThat(decoded.getAlgorithm()).isEqualTo("RSA");
        assertThat(decoded.getEncoded()).isEqualTo(keyPair.getPublic().getEncoded());
    }

    @Test
    void decodePrivateKey_throwsForInvalidBase64() {
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> UtilPairKey.decodePrivateKey("!!!not-base64!!!"))).isNotNull();
    }
}
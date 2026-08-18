package co.edu.uco.infraestructure.secondaryadapters.encryption;

import co.edu.uco.application.primaryports.dto.keypair.KeyPairDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.*;

@Service
public final class JavaSecurityEncryptTokenAdapter implements EncryptTokenPort {

    private final LoggingPort log;
    private final CatalogPort catalogPort;

    public JavaSecurityEncryptTokenAdapter(CatalogPort catalogPort, LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(JavaSecurityEncryptTokenAdapter.class);
        this.catalogPort = catalogPort;
    }

    @Override
    public KeyPairDTO generateKeys() {
        try {
            var generator = KeyPairGenerator.getInstance(ALGORITHM_GENERATE_PAIR_KEY);
            generator.initialize(PAIR_KEY_SIZE);
            var keyPair = generator.generateKeyPair();
            return new KeyPairDTO(keyPair.getPublic(), keyPair.getPrivate());
        } catch (Exception e) {
            var message = catalogPort.getMessage("TCH_026");
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(message, catalogPort.getMessage("FUN_025"), e,
                    ExceptionType.TECHNICAL);
        }
    }

    @Override
    public String generateSignature(String data, PublicKey publicKey) {
        try{
            var encryptCipher = Cipher.getInstance(ALGORITHM_PAIR_KEY);
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedData = encryptCipher.doFinal(dataBytes);

            return Base64.getEncoder().encodeToString(encryptedData);
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                BadPaddingException e){
            var message = catalogPort.getMessage("TCH_027");
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(message, catalogPort.getMessage("FUN_025"), e,
                    ExceptionType.TECHNICAL);
        }
    }

    @Override
    public Boolean access(String privateKey, String signature, String secretName) {
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);

        var keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        try{
            var decryptCipher = Cipher.getInstance(ALGORITHM_PAIR_KEY);
            var keyFactory = KeyFactory.getInstance(ALGORITHM_GENERATE_PAIR_KEY);
            decryptCipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(keySpec));

            byte[] decryptedData = decryptCipher.doFinal(signatureBytes);
            var data = new String(decryptedData, StandardCharsets.UTF_8);

            return data.equals(secretName);
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeySpecException e){
            log.error(catalogPort.getMessage("TCH_028"),privateKey,signature,secretName, e);
            return false;
        }
    }
}

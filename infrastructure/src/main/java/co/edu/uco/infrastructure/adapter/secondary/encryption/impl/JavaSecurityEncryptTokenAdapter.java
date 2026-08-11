package co.edu.uco.infrastructure.adapter.secondary.encryption.impl;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.application.dto.keypair.KeyPairDTO;
import co.edu.uco.core.domain.port.out.secret.EncryptTokenPort;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.exception.enumeration.ExceptionType;
import lombok.extern.slf4j.Slf4j;
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

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;

@Slf4j
@Service
public final class JavaSecurityEncryptTokenAdapter implements EncryptTokenPort {
    private final InMemoryCatalog inMemoryCatalog;
    public JavaSecurityEncryptTokenAdapter(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public KeyPairDTO generateKeys() {
        try {
            var generator = KeyPairGenerator.getInstance(ALGORITHM_GENERATE_PAIR_KEY);
            generator.initialize(PAIR_KEY_SIZE);
            var keyPair = generator.generateKeyPair();
            return new KeyPairDTO(keyPair.getPublic(), keyPair.getPrivate());
        } catch (Exception e) {
            var message = inMemoryCatalog.getContent(MessageKeyEnum.TCH_026.getKey());
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(message, inMemoryCatalog.getContent(MessageKeyEnum.FUN_025.getKey()), e, ExceptionType.TECHNICAL);
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
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            var message = inMemoryCatalog.getContent(MessageKeyEnum.TCH_027.getKey());
            log.error(message, e);
            throw CrossWordsException.buildInfrastructure(message, inMemoryCatalog.getContent(MessageKeyEnum.FUN_025.getKey()), e, ExceptionType.TECHNICAL);
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
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e){
            log.error(inMemoryCatalog.getContent(MessageKeyEnum.TCH_028.getKey()),privateKey,signature,secretName, e);
            return false;
        }
    }
}
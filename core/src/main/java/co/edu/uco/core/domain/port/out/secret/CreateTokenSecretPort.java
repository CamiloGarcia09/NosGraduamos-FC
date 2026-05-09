package co.edu.uco.core.domain.port.out.secret;

public interface CreateTokenSecretPort {
    void execute(String tokenId, String tokenEncrypted);
}

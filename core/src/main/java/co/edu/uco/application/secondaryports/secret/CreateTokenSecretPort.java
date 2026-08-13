package co.edu.uco.application.secondaryports.secret;

public interface CreateTokenSecretPort {
    void execute(String tokenId, String tokenEncrypted);
}

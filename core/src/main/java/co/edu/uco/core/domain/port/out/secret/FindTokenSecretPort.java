package co.edu.uco.core.domain.port.out.secret;

public interface FindTokenSecretPort {
    String execute(String secretName);
}

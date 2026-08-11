package co.edu.uco.core.domain.port.out.vault;

public interface VaultPort {

    String getSecretValue(String secretName);

}
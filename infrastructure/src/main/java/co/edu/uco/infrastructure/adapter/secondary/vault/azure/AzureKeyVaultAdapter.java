package co.edu.uco.infrastructure.adapter.secondary.vault.azure;

import co.edu.uco.core.domain.port.out.vault.VaultPort;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AzureKeyVaultAdapter implements VaultPort {

    private final SecretClient secretClient;

    public AzureKeyVaultAdapter(@Value("${azure.keyvault.url}") String urlVault){
        this.secretClient = new SecretClientBuilder().vaultUrl(urlVault)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Override
    public String getSecretValue(String secretName) {
        return secretClient.getSecret(secretName).getValue();
    }
}


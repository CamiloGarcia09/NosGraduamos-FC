package co.edu.uco.infraestructure.secondaryadapters.vault.azure;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.vault.VaultPort;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionType;
import com.azure.core.exception.ClientAuthenticationException;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

@Component
public class AzureKeyVaultAdapter implements VaultPort {

    private final LoggingPort log;
    private final CatalogPort catalogPort;
    private final SecretClient secretClient;

    public AzureKeyVaultAdapter(
            @Value("${azure.keyvault.url:}") String urlVault,
            CatalogPort catalogPort,
            LoggingPortFactory loggerFactory
    ) {
        this.log = loggerFactory.getLogger(AzureKeyVaultAdapter.class);
        this.catalogPort = catalogPort;

        if (isEmptyOrNull(trim(urlVault))) {
            log.error("Azure Key Vault URL is missing or empty.");
            this.secretClient = null;
        } else {
            SecretClient client = null;
            try {
                client = new SecretClientBuilder()
                        .vaultUrl(urlVault)
                        .credential(new DefaultAzureCredentialBuilder().build())
                        .buildClient();
            } catch (Exception ex) {
                log.error("Failed to initialize Azure SecretClient with URL: " + urlVault, ex);
            }
            this.secretClient = client;
        }
    }

    @Override
    public String getSecretValue(String secretName) {
        if (isEmptyOrNull(trim(secretName))) {
            var techMsg = "Secret name parameter cannot be null or empty.";
            log.error(techMsg);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ExceptionType.TECHNICAL
            );
        }

        if (this.secretClient == null) {
            var techMsg = "Azure SecretClient is not initialized or Key Vault URL was not provided.";
            log.error(techMsg);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ExceptionType.TECHNICAL
            );
        }

        try {
            KeyVaultSecret secret = secretClient.getSecret(secretName);

            if (secret == null || isEmptyOrNull(secret.getValue())) {
                var techMsg = "Secret with name '" + secretName + "' exists but has a null or empty value.";
                log.error(techMsg);
                throw CrossWordsException.buildInfrastructure(
                        techMsg,
                        catalogPort.getMessage("FUN_023"),
                        ExceptionType.TECHNICAL
                );
            }

            if (secret.getProperties() != null && Boolean.FALSE.equals(secret.getProperties().isEnabled())) {
                var techMsg = "Secret with name '" + secretName + "' is disabled in Azure Key Vault.";
                log.error(techMsg);
                throw CrossWordsException.buildInfrastructure(
                        techMsg,
                        catalogPort.getMessage("FUN_023"),
                        ExceptionType.TECHNICAL
                );
            }

            return secret.getValue();

        } catch (ResourceNotFoundException ex) {
            var techMsg = "Secret '" + secretName + "' not found in Azure Key Vault (404 Not Found).";
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (ClientAuthenticationException ex) {
            var techMsg = "Authentication or permission failure accessing Azure Key Vault for secret: " + secretName;
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (HttpResponseException ex) {
            var techMsg = "HTTP error (" + ex.getResponse().getStatusCode() + ") communicating with Azure Key Vault.";
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            var techMsg = "Unexpected error retrieving secret '" + secretName + "' from Azure Key Vault.";
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage("FUN_023"),
                    ex,
                    ExceptionType.TECHNICAL
            );
        }
    }
}



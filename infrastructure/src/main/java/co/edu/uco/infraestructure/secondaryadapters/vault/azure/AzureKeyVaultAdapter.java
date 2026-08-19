package co.edu.uco.infraestructure.secondaryadapters.vault.azure;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.vault.VaultPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCode;
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

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
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
            log.error(catalogPort.getMessage(MessageCatalogCode.TCH_041));
            this.secretClient = null;
        } else {
            SecretClient client = null;
            try {
                client = new SecretClientBuilder()
                        .vaultUrl(urlVault)
                        .credential(new DefaultAzureCredentialBuilder().build())
                        .buildClient();
            } catch (Exception ex) {
                log.error(catalogPort.getMessage(MessageCatalogCode.TCH_042).formatted(urlVault), ex);
            }
            this.secretClient = client;
        }
    }

    @Override
    public String getSecretValue(String secretName) {
        if (isEmptyOrNull(trim(secretName))) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_043);
            log.error(techMsg);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ExceptionType.TECHNICAL
            );
        }

        if (isNullObject(this.secretClient)) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_044);
            log.error(techMsg);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ExceptionType.TECHNICAL
            );
        }

        try {
            KeyVaultSecret secret = secretClient.getSecret(secretName);

            if (isNullObject(secret) || isEmptyOrNull(secret.getValue())) {
                var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_045).formatted(secretName);
                log.error(techMsg);
                throw CrossWordsException.buildInfrastructure(
                        techMsg,
                        catalogPort.getMessage(MessageCatalogCode.FUN_023),
                        ExceptionType.TECHNICAL
                );
            }

            if (!isNullObject(secret.getProperties()) && Boolean.FALSE.equals(secret.getProperties().isEnabled())) {
                var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_046).formatted(secretName);
                log.error(techMsg);
                throw CrossWordsException.buildInfrastructure(
                        techMsg,
                        catalogPort.getMessage(MessageCatalogCode.FUN_023),
                        ExceptionType.TECHNICAL
                );
            }

            return secret.getValue();

        } catch (ResourceNotFoundException ex) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_047).formatted(secretName);
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (ClientAuthenticationException ex) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_048).formatted(secretName);
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (HttpResponseException ex) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_049).formatted(ex.getResponse().getStatusCode());
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ex,
                    ExceptionType.TECHNICAL
            );

        } catch (CrossWordsException ex) {
            throw ex;
        } catch (Exception ex) {
            var techMsg = catalogPort.getMessage(MessageCatalogCode.TCH_050).formatted(secretName);
            log.error(techMsg, ex);
            throw CrossWordsException.buildInfrastructure(
                    techMsg,
                    catalogPort.getMessage(MessageCatalogCode.FUN_023),
                    ex,
                    ExceptionType.TECHNICAL
            );
        }
    }
}

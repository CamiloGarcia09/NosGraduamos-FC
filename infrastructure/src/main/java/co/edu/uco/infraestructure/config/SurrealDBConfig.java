package co.edu.uco.infraestructure.config;

import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SurrealDBConfig {

    private final SurrealDBProperties surrealDBProperties;

    public SurrealDBConfig(SurrealDBProperties surrealDBProperties) {
        this.surrealDBProperties = surrealDBProperties;
    }

    @Bean
    public Surreal surreal() throws Exception {

        String connectionUrl = String.format(
                "ws://%s:%d/rpc",
                surrealDBProperties.getHost(),
                surrealDBProperties.getPort()
        );

        log.info("Connecting to SurrealDB at: {}", connectionUrl);

        Surreal surreal = new Surreal();

        // Conectar
        surreal.connect(connectionUrl);

        // Autenticación root
        surreal.signin(
                new Root(
                        surrealDBProperties.getUsername(),
                        surrealDBProperties.getPassword()
                )
        );

        log.info("Successfully signed in to SurrealDB");

        // Seleccionar namespace
        surreal.useNs(
                surrealDBProperties.getNamespace()
        );

        // Seleccionar database
        surreal.useDb(
                surrealDBProperties.getDatabase()
        );

        log.info(
                "Using namespace: {} and database: {}",
                surrealDBProperties.getNamespace(),
                surrealDBProperties.getDatabase()
        );

        return surreal;
    }
}
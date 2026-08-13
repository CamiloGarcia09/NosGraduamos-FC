package co.edu.uco.infraestructure.config;

import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SurrealDBConfig {

    private final LoggingPort log;
    private final SurrealDBProperties surrealDBProperties;

    public SurrealDBConfig(SurrealDBProperties surrealDBProperties, LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(SurrealDBConfig.class);
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

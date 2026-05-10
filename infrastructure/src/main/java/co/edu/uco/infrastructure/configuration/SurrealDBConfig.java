package co.edu.uco.infrastructure.configuration;

import com.surrealdb.connection.SurrealConnection;
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
    public SurrealConnection surrealConnection() throws Exception {
        String connectionUrl = String.format(
            "ws://%s:%d",
            surrealDBProperties.getHost(),
            surrealDBProperties.getPort()
        );

        log.info("Connecting to SurrealDB at: {}", connectionUrl);

        SurrealConnection connection = new SurrealConnection(connectionUrl);

        // Sign in
        connection.signin(
            surrealDBProperties.getUsername(),
            surrealDBProperties.getPassword()
        );
        log.info("Successfully signed in to SurrealDB");

        // Use namespace and database
        connection.use(
            surrealDBProperties.getNamespace(),
            surrealDBProperties.getDatabase()
        );
        log.info("Using namespace: {} and database: {}", 
            surrealDBProperties.getNamespace(), 
            surrealDBProperties.getDatabase());

        return connection;
    }
}
